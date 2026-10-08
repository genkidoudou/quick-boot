package io.github.genkidoudou.core.mybatisplis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 扩展配置：注册审计字段自动填充与分页等插件。
 */
@Configuration
public class MybatisPlusAutoConfiguration {

    /**
     * 注册 {@link MyMetaObjectHandler}，在 INSERT/UPDATE 时填充 {@link io.github.genkidoudou.core.entity.BaseEntity} 审计列。
     *
     * @return MetaObjectHandler Bean
     */
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 注册分页插件，并挂载容器中其它 {@link InnerInterceptor}（如慢 SQL mapperId 标记）。
     * <p>
     * 未注册 {@link PaginationInnerInterceptor} 时，{@code page()} 不会执行 COUNT，
     * 表现为 {@code records} 有数据但 {@code total=0}。
     *
     * @param innerInterceptors 其它内层拦截器（可为空）
     * @return MyBatis-Plus 插件链
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        innerInterceptors.orderedStream().forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }
}
