package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.QuizReviewList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizReviewListRepository extends JpaRepository<QuizReviewList, Long> {
    boolean existsByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
    List<QuizReviewList> findByUser_UserId(Long userId);
}
