package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.UserQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {
    List<UserQuiz> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
    Optional<UserQuiz> findByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);
    @Query("SELECT COUNT(uq) FROM UserQuiz uq WHERE uq.user.userId = :userId AND uq.solvedDate = :date")
    int countByUserIdAndSolvedDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT uq.quiz.quizId) FROM UserQuiz uq WHERE uq.user.userId = :userId")
    long countDistinctByUserId(@Param("userId") Long userId);

    @Query("SELECT uq.quiz FROM UserQuiz uq WHERE uq.solvedDate = :date AND uq.isCorrect = false GROUP BY uq.quiz ORDER BY COUNT(uq) DESC")
    List<Quiz> findTop5MostWrongOnDate(@Param("date") LocalDate date);

    boolean existsByUser_UserIdAndQuiz_QuizIdAndSolvedDate(Long userId, Long quizId, LocalDate solvedDate);
}
