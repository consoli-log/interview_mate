package com.interviewmate.be.infrastructure.persistence.question;

import com.interviewmate.be.auth.domain.User;
import com.interviewmate.be.common.config.TestEnvConfig;
import com.interviewmate.be.prompt.domain.Prompt;
import com.interviewmate.be.question.domain.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.interviewmate.be.infrastructure.persistence.question
 * fileName       : QuestionRepositoryTest
 * author         : eumsoli
 * date           : 2025-04-01
 * description    : QuestionRepository의 쿼리 메서드를 테스트하는 클래스
 */
@DataJpaTest
@Import(TestEnvConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Prompt prompt;
    private Question activeQuestionOne;
    private Question activeQuestionTwo;
    private Question inactiveQuestion;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("soli@test.com")
                .name("테스트 사용자")
                .provider("google")
                .providerId("1234567890")
                .build();
        entityManager.persist(user);

        prompt = Prompt.builder()
                .user(user)
                .title("프롬프트 제목")
                .prompt("프롬프트 내용")
                .build();
        entityManager.persist(prompt);

        activeQuestionOne = Question.builder().prompt(prompt).number(1).question("질문1").build();
        activeQuestionTwo = Question.builder().prompt(prompt).number(2).question("질문2").build();
        inactiveQuestion = Question.builder().prompt(prompt).number(3).question("질문3").build();
        inactiveQuestion.deactivate(); // 비활성화된 질문

        entityManager.persist(activeQuestionOne);
        entityManager.persist(activeQuestionTwo);
        entityManager.persist(inactiveQuestion);
        entityManager.flush();
    }

    @Test
    @DisplayName("프롬프트에 대한 가장 큰 질문 번호를 조회한다")
    void findMaxNumberByPrompt_ReturnsHighestActiveNumber() {
        int max = questionRepository.findMaxNumberByPrompt(prompt);

        assertThat(max).isEqualTo(2);
    }

    @Test
    @DisplayName("비활성화된 질문 존재 여부를 반환한다")
    void existsByPromptAndIsActiveFalse_ReturnsTrue() {
        boolean exists = questionRepository.existsByPromptAndIsActiveFalse(prompt);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("활성화된 질문을 모두 조회한다")
    void findAllByPromptAndIsActiveTrue_ReturnsOnlyActiveQuestions() {
        List<Question> list = questionRepository.findAllByPromptAndIsActiveTrue(prompt);

        assertThat(list).hasSize(2);
        assertThat(list).extracting("question").contains("질문1", "질문2");
    }

    @Test
    @DisplayName("질문 번호 순으로 활성화된 질문을 조회한다")
    void findAllByPromptAndIsActiveTrueOrderByNumber_ReturnsOrderedList() {
        List<Question> list = questionRepository.findAllByPromptAndIsActiveTrueOrderByNumber(prompt);

        assertThat(list.get(0).getNumber()).isEqualTo(1);
        assertThat(list.get(1).getNumber()).isEqualTo(2);
    }

    // TODO : 실제 데이터 생성 후 다시 확인
    @Test
    @DisplayName("1개월 이상된 비활성 질문을 삭제한다")
    void deleteByIsActiveFalseAndUpdatedAtBefore_DeletesOldInactiveQuestions() {
        // 기준 시각
        LocalDateTime expiredTime = LocalDateTime.now().minusMonths(2);

        // 1. 기존 질문을 불러오기
        Question target = entityManager.find(Question.class, inactiveQuestion.getId());

        // 2. 비활성화 처리 + updatedAt 수동 설정 (JPQL 조건 만족시키기 위함)
        target.deactivate();
        ReflectionTestUtils.setField(target, "updatedAt", expiredTime); // 2개월 전

        // 3. 다시 저장
        entityManager.persistAndFlush(target);
        entityManager.clear();

        // 4. 삭제 실행
        int deletedCount = questionRepository.deleteByIsActiveFalseAndUpdatedAtBefore(LocalDateTime.now().minusMonths(1));

        assertThat(deletedCount).isGreaterThanOrEqualTo(1);
    }

}