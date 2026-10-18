package io.github.genkidoudou.system.internal.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.file.FileClassifyRule;
import io.github.genkidoudou.common.file.FileClassifyRuleResolver;
import io.github.genkidoudou.system.internal.entity.SysFileClassify;
import io.github.genkidoudou.system.internal.mapper.SysFileClassifyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 从 {@code sys_file_classify} 解析上传分类规则。
 */
@Component
@RequiredArgsConstructor
public class DbFileClassifyRuleResolver implements FileClassifyRuleResolver {

    private final SysFileClassifyMapper sysFileClassifyMapper;

    /**
     * 按分类编码查询规则，含停用记录。
     *
     * @param classify 分类编码
     * @return 规则；不存在时为空
     */
    @Override
    public Optional<FileClassifyRule> findByClassify(String classify) {
        if (!StringUtils.hasText(classify)) {
            return Optional.empty();
        }
        SysFileClassify row = sysFileClassifyMapper.selectOne(new LambdaQueryWrapper<SysFileClassify>()
                .eq(SysFileClassify::getClassifyCode, classify.trim())
                .last("limit 1"));
        return Optional.ofNullable(row).map(this::toRule);
    }

    /**
     * 列出启用中的分类。
     *
     * @return 状态为正常的规则
     */
    @Override
    public List<FileClassifyRule> listEnabled() {
        return sysFileClassifyMapper.selectList(new LambdaQueryWrapper<SysFileClassify>()
                        .eq(SysFileClassify::getStatus, "0")
                        .orderByAsc(SysFileClassify::getClassifyCode))
                .stream()
                .map(this::toRule)
                .toList();
    }

    /**
     * 表记录转为运行时规则。
     * <p>
     * {@code COMMON_IS} 中「是」的字典值是 {@code 0}。
     *
     * @param row 分类实体
     * @return 规则
     */
    private FileClassifyRule toRule(SysFileClassify row) {
        FileClassifyRule rule = new FileClassifyRule();
        rule.setClassify(row.getClassifyCode());
        rule.setClassifyName(row.getClassifyName());
        rule.setLimitExt(row.getAllowExt());
        if (row.getMaxSizeBytes() != null) {
            rule.setLimitSizeBytes(row.getMaxSizeBytes());
        }
        if (row.getMaxCount() != null) {
            rule.setLimitCount(row.getMaxCount());
        }
        rule.setStatus(row.getStatus());
        rule.setAnonymous("0".equals(row.getAnonymous()));
        return rule;
    }
}
