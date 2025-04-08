package com.interviewmate.be.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.interviewmate.be.common.config
 * fileName       : GeminiConfig
 * author         : eumsoli
 * date           : 2025-04-05
 * description    : Gemini API 설정 클래스
 */
@Getter
@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

}
