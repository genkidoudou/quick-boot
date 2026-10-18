package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysFile;
import io.github.genkidoudou.system.internal.vo.SysFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务。
 */
public interface ISysFileService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysFileVo> pageVo(PageRequest<SysFileVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysFileVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysFile saveVo(SysFileVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysFileVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 按分类上传并登记文件记录。
     *
     * @param file         上传文件
     * @param classifyCode 分类编码
     * @return 登记后的文件视图（路径已是可访问地址）
     */
    SysFileVo upload(MultipartFile file, String classifyCode);

    /**
     * 查询指定文件分类下的文件个数
     *
     * @param classifyCode 文件分类编码
     * @return
     * @since 2026/10/18
     */
    Long count(String classifyCode);
}
