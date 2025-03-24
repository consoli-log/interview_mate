package com.interviewmate.be.auth.application;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import com.interviewmate.be.infrastructure.persistence.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.interviewmate.be.auth.application
 * fileName       : UserService
 * author         : eumsoli
 * date           : 2025-03-21
 * description    : 사용자 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * methodName : deleteUser
     * description : 사용자 삭제 처리
     *
     * @param user 삭제할 사용자
     */
    public void deleteUser(User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.delete(user);
    }
}
