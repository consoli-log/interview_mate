package com.interviewmate.be.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.interviewmate.be.auth.dto
 * fileName       : SignupRequest
 * author         : eumsoli
 * date           : 2025-03-17
 * description    : 회원가입 요청 DTO
 */
@Getter
@Setter
public class SignupRequest {
    private String email;
    private String name;
    private String provider;
}
