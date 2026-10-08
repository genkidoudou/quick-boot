package io.github.genkidoudou.common.excel.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.genkidoudou.common.excel.annotation.ExcelDictFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 导入模板列约束：扫描、提示、下拉与降级路径最小验证。
 */
class TemplateConstraintWriteHandlerTest {

  @Test
  void scanner_declarationOrder_whenNoIndex() {
    List<ExcelPropertyColumn> cols = ExcelPropertyColumnScanner.scan(SampleImportRow.class);
    assertEquals(3, cols.size());
    assertEquals("userName", cols.get(0).field().getName());
    assertEquals(0, cols.get(0).columnIndex());
    assertEquals("sex", cols.get(1).field().getName());
    assertEquals(1, cols.get(1).columnIndex());
    assertEquals("phonenumber", cols.get(2).field().getName());
    assertEquals(2, cols.get(2).columnIndex());
  }

  @Test
  void dictLabels_inline() {
    ExcelDictFormat format = SampleImportRow.class.getDeclaredFields()[1].getAnnotation(ExcelDictFormat.class);
    DictLabelResolver.ResolveResult result = DictLabelResolver.resolve(format, "sex");
    assertTrue(result.hasLabels());
    assertEquals(List.of("男", "女"), result.labels());
    assertFalse(result.skippedWithWarn());
  }

  @Test
  void dictLabels_lookupMissing_skipsWithWarn() {
    ExcelDictFormat format = DictTypeRow.class.getDeclaredFields()[0].getAnnotation(ExcelDictFormat.class);
    DictLabelResolver.ResolveResult result = DictLabelResolver.resolve(format, "status");
    assertFalse(result.hasLabels());
    assertTrue(result.skippedWithWarn());
  }

  @Test
  void validationPrompt_order() throws Exception {
    var field = SampleImportRow.class.getDeclaredField("phonenumber");
    String prompt = ValidationPromptBuilder.build(field);
    assertNotNull(prompt);
    assertTrue(prompt.contains("手机号"));
    assertTrue(prompt.contains("1[3-9]"));
  }

  @Test
  void validationPrompt_coversCommonAnnotations() throws Exception {
    var field = RichValidationRow.class.getDeclaredField("age");
    String prompt = ValidationPromptBuilder.build(field);
    assertNotNull(prompt);
    assertTrue(prompt.contains("年龄不能为空"));
    assertTrue(prompt.contains("18") || prompt.contains("≥"));
    assertTrue(prompt.contains("120") || prompt.contains("≤"));
  }

  @Test
  void write_withConstraints_hasValidations() throws Exception {
    byte[] bytes = write(SampleImportRow.class, true);
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      Sheet sheet = workbook.getSheetAt(0);
      List<? extends DataValidation> validations = sheet.getDataValidations();
      assertFalse(validations.isEmpty(), "应写入数据有效性/提示");
      boolean hasList = validations.stream().anyMatch(v -> {
        String[] list = v.getValidationConstraint().getExplicitListValues();
        return list != null && list.length >= 2;
      });
      assertTrue(hasList, "性别列应有显式下拉");
    }
  }

  @Test
  void write_requiredHead_isRed() throws Exception {
    byte[] bytes = write(SampleImportRow.class, true);
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      Sheet sheet = workbook.getSheetAt(0);
      // 用户账号：NotBlank → 红；性别：无必填 → 非红
      var requiredFont = workbook.getFontAt(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
      var optionalFont = workbook.getFontAt(sheet.getRow(0).getCell(1).getCellStyle().getFontIndex());
      assertEquals(IndexedColors.RED.getIndex(), requiredFont.getColor());
      assertTrue(requiredFont.getBold());
      assertNotEquals(IndexedColors.RED.getIndex(), optionalFont.getColor());
    }
  }

  @Test
  void write_withoutHandler_noValidations() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    EasyExcel.write(os, SampleImportRow.class)
      .sheet("t")
      .doWrite(Collections.emptyList());
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(os.toByteArray()))) {
      assertTrue(workbook.getSheetAt(0).getDataValidations().isEmpty());
    }
  }

  @Test
  void write_oversizedInline_degradesSafely() throws Exception {
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < 40; i++) {
      labels.add("选项标签编号" + i + "_填充字符填充字符填充");
    }
    assertTrue(String.join(",", labels).length() > TemplateConstraintWriteHandler.EXPLICIT_LIST_CHAR_LIMIT);

    byte[] bytes = write(OversizedInlineRow.class, true);
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      Sheet sheet = workbook.getSheetAt(0);
      assertFalse(sheet.getDataValidations().isEmpty());
      boolean hasHidden = false;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        if (workbook.isSheetHidden(i) || workbook.isSheetVeryHidden(i)) {
          hasHidden = true;
          break;
        }
      }
      assertTrue(hasHidden, "超长下拉应使用隐藏 sheet");
    }
  }

  private static byte[] write(Class<?> head, boolean constraints) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    var writer = EasyExcel.write(os, head).inMemory(Boolean.TRUE).sheet("t");
    if (constraints) {
      writer.registerWriteHandler(new TemplateConstraintWriteHandler(head));
    }
    writer.doWrite(Collections.emptyList());
    return os.toByteArray();
  }

  public static class SampleImportRow {
    @NotBlank(message = "用户账号不能为空")
    @ExcelProperty("用户账号")
    private String userName;

    @ExcelDictFormat(dictText = {"0=男", "1=女"})
    @ExcelProperty("性别")
    private String sex;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ExcelProperty("手机号")
    private String phonenumber;
  }

  public static class DictTypeRow {
    @ExcelDictFormat(dictType = "sys_normal_disable")
    @ExcelProperty("状态")
    private String status;
  }

  public static class RichValidationRow {
    @NotNull(message = "年龄不能为空")
    @Min(18)
    @Max(120)
    @ExcelProperty("年龄")
    private Integer age;

    @Size(min = 1, max = 32)
    @ExcelProperty("编码")
    private String code;
  }

  public static class OversizedInlineRow {
    @ExcelDictFormat(dictText = {
      "0=选项标签编号0_填充字符填充字符填充",
      "1=选项标签编号1_填充字符填充字符填充",
      "2=选项标签编号2_填充字符填充字符填充",
      "3=选项标签编号3_填充字符填充字符填充",
      "4=选项标签编号4_填充字符填充字符填充",
      "5=选项标签编号5_填充字符填充字符填充",
      "6=选项标签编号6_填充字符填充字符填充",
      "7=选项标签编号7_填充字符填充字符填充",
      "8=选项标签编号8_填充字符填充字符填充",
      "9=选项标签编号9_填充字符填充字符填充",
      "10=选项标签编号10_填充字符填充字符填充",
      "11=选项标签编号11_填充字符填充字符填充",
      "12=选项标签编号12_填充字符填充字符填充",
      "13=选项标签编号13_填充字符填充字符填充",
      "14=选项标签编号14_填充字符填充字符填充",
      "15=选项标签编号15_填充字符填充字符填充",
      "16=选项标签编号16_填充字符填充字符填充",
      "17=选项标签编号17_填充字符填充字符填充",
      "18=选项标签编号18_填充字符填充字符填充",
      "19=选项标签编号19_填充字符填充字符填充",
      "20=选项标签编号20_填充字符填充字符填充",
      "21=选项标签编号21_填充字符填充字符填充",
      "22=选项标签编号22_填充字符填充字符填充",
      "23=选项标签编号23_填充字符填充字符填充",
      "24=选项标签编号24_填充字符填充字符填充",
      "25=选项标签编号25_填充字符填充字符填充",
      "26=选项标签编号26_填充字符填充字符填充",
      "27=选项标签编号27_填充字符填充字符填充",
      "28=选项标签编号28_填充字符填充字符填充",
      "29=选项标签编号29_填充字符填充字符填充",
      "30=选项标签编号30_填充字符填充字符填充",
      "31=选项标签编号31_填充字符填充字符填充",
      "32=选项标签编号32_填充字符填充字符填充",
      "33=选项标签编号33_填充字符填充字符填充",
      "34=选项标签编号34_填充字符填充字符填充",
      "35=选项标签编号35_填充字符填充字符填充",
      "36=选项标签编号36_填充字符填充字符填充",
      "37=选项标签编号37_填充字符填充字符填充",
      "38=选项标签编号38_填充字符填充字符填充",
      "39=选项标签编号39_填充字符填充字符填充"
    })
    @ExcelProperty("超长选项")
    private String option;
  }
}
