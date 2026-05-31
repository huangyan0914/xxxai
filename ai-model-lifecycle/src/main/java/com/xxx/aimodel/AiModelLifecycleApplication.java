package com.xxx.aimodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI模型全周期生命周期管理模块入口
 * 负责：模型开发、训练、部署与迭代全生命周期
 */
@MapperScan({"com.xxx.**.mapper"})
@SpringBootApplication(scanBasePackages = "com.xxx")
public class AiModelLifecycleApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiModelLifecycleApplication.class, args);
    }
}

