package com.interviewmate.be.question.presentation;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.question.application.PromptService;
import com.interviewmate.be.question.dto.PromptListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.interviewmate.be.question.presentation
 * fileName       : PromptController
 * author         : eumsoli
 * date           : 2025-03-24
 * description    : 프롬프트 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    /**
     * methodName : getPromptList
     * description : 로그인 사용자의 프롬프트 목록을 조회하는 API
     *
     * @param user 로그인 사용자
     * @return ResponseEntity<List<PromptListResponse>> 프롬프트 목록 응답
     */
    @GetMapping
    public ResponseEntity<List<PromptListResponse>> getPromptList(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        List<PromptListResponse> response = promptService.getPromptList(user);

        return ResponseEntity.ok(response);
    }

}
