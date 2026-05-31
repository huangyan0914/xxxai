package com.xxx.system.tool.excel.dict.config;

import com.xxx.system.tool.excel.dict.core.DictFrameworkUtils;
import com.xxx.system.service.DictService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CetcDictAutoConfiguration {
    @Bean
    public DictFrameworkUtils dictUtils(DictService dictService) {
        DictFrameworkUtils.init(dictService);
        return new DictFrameworkUtils();
    }
}


