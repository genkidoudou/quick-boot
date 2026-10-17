package io.github.genkidoudou.common.datascope;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Configuration
public class DataPermissionConfiguration {


    @Bean
    public DataPermissionAspect dataPermissionAspect(DataScopeSupport dataScopeSupport) {
        return new DataPermissionAspect(dataScopeSupport);
    }


}
