package com.interviewmate.be.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.interviewmate.be.config
 * fileName       : TestEnvConfig
 * author         : eumsoli
 * date           : 2025-03-21
 * description    : 테스트 환경에서 환경 변수 로드
 */

@Configuration
public class TestEnvConfig {

    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

}
