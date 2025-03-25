package com.interviewmate.be.question.presentation;

import com.interviewmate.be.question.application.PromptService;
import com.interviewmate.be.question.dto.PromptRequest;
import com.interviewmate.be.question.dto.PromptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
