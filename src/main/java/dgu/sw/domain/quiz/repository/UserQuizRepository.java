package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.UserQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {
    List<UserQuiz> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
    Optional<UserQuiz> findByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
}
