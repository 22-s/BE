package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.QuizReviewList;
import dgu.sw.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizReviewListRepository extends JpaRepository<QuizReviewList, Long> {
    boolean existsByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
    List<QuizReviewList> findByUser_UserId(Long userId);
    Optional<QuizReviewList> findByUserAndQuiz(User user, Quiz quiz);
}
