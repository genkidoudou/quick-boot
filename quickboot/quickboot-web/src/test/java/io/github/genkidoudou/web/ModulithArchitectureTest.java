package io.github.genkidoudou.web;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 模块边界约定：
 * <ul>
 *   <li>{@code system.api.**}：对外可见（含子包，无需逐包 NamedInterface）</li>
 *   <li>{@code system.internal.**}：禁止被 system 以外模块引用</li>
 * </ul>
 * {@link ApplicationModules#verify()} 负责模块间 allowedDependencies；
 * ArchUnit + ASM 负责强制「禁止 internal」（含局部变量表漏检场景）。
 */
class ModulithArchitectureTest {

    private static final String SYSTEM_INTERNAL = "io/github/genkidoudou/system/internal";

    private static final ApplicationModules MODULES = ApplicationModules.of("io.github.genkidoudou");

    @Test
    void verifyModularStructure() {
        MODULES.verify();
    }

    @Test
    void outsideSystemMustNotDependOnSystemInternal() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("io.github.genkidoudou");

        ArchRule rule = com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackage("io.github.genkidoudou.system..")
                .should().dependOnClassesThat().resideInAPackage("io.github.genkidoudou.system.internal..")
                .because("system.internal.** 禁止对外；请改用 system.api.**");

        rule.check(classes);
    }

    /**
     * 互补：扫描 web 字节码 LocalVariableTable 等，抓住 ArchUnit 漏掉的 internal 引用。
     */
    @Test
    void webBytecodeMustNotMentionSystemInternal() throws IOException {
        Path classesDir = Path.of("target", "classes", "io", "github", "genkidoudou", "web");
        if (!Files.isDirectory(classesDir)) {
            fail("找不到编译输出目录: " + classesDir.toAbsolutePath() + "（请先 compile）");
        }

        List<String> violations = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(classesDir)) {
            stream.filter(p -> p.toString().endsWith(".class")).forEach(path -> {
                try {
                    scanClassFile(path, violations);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        if (!violations.isEmpty()) {
            fail("web 字节码中出现 system.internal 引用（含局部变量表）:\n - "
                    + String.join("\n - ", violations));
        }
    }

    @Test
    void writeModuleDocumentation() {
        new Documenter(MODULES).writeDocumentation();
    }

    private static void scanClassFile(Path classFile, List<String> violations) throws IOException {
        byte[] bytes = Files.readAllBytes(classFile);
        ClassReader reader = new ClassReader(bytes);
        String className = reader.getClassName().replace('/', '.');

        reader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                checkDesc(className + "#" + name, descriptor, signature, violations);
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature,
                    String[] exceptions) {
                checkDesc(className + "#" + methodName, descriptor, signature, violations);
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        checkType(className + "#" + methodName, type, violations);
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        checkType(className + "#" + methodName, owner, violations);
                        checkDesc(className + "#" + methodName, descriptor, null, violations);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor,
                            boolean isInterface) {
                        checkType(className + "#" + methodName, owner, violations);
                        checkDesc(className + "#" + methodName, descriptor, null, violations);
                    }

                    @Override
                    public void visitLocalVariable(String name, String descriptor, String signature,
                            org.objectweb.asm.Label start, org.objectweb.asm.Label end, int index) {
                        checkDesc(className + "#" + methodName + " local " + name, descriptor, signature, violations);
                    }
                };
            }
        }, ClassReader.SKIP_FRAMES);
    }

    private static void checkDesc(String where, String descriptor, String signature, List<String> violations) {
        if (descriptor != null && descriptor.contains(SYSTEM_INTERNAL)) {
            violations.add(where + " descriptor=" + descriptor);
        }
        if (signature != null && signature.contains(SYSTEM_INTERNAL)) {
            violations.add(where + " signature=" + signature);
        }
    }

    private static void checkType(String where, String internalName, List<String> violations) {
        if (internalName != null && internalName.startsWith(SYSTEM_INTERNAL)) {
            violations.add(where + " type=" + internalName.replace('/', '.'));
        }
    }
}
