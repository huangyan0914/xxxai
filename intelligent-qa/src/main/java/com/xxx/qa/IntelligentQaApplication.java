package com.xxx.qa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能问答模块入口
 * 负责：基于AI的智能问答与知识检索
 */
@MapperScan({"com.xxx.**.mapper"})
@SpringBootApplication(scanBasePackages = "com.xxx")
public class IntelligentQaApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntelligentQaApplication.class, args);
    }
}

