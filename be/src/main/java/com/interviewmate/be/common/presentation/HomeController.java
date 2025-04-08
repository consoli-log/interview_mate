package com.interviewmate.be.common.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.interviewmate.be.common.presentation
 * fileName       : HomeController
 * author         : eumsoli
 * date           : 2025-04-09
 * description    : 홈 컨트롤러
 */
@RestController
@Tag(name = "Home", description = "홈 API")
public class HomeController {

    /**
     * methodName : home
     * description : 홈 API
     *
     * @return "Welcome to Interview Mate!" 메시지
     */
    @GetMapping("/")
    public String home() {
        return "Welcome to Interview Mate!";
    }

}


