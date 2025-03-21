package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCategory(String category);
    List<Quiz> findByQuestionContainingOrDescriptionContaining(String questionKeyword, String descriptionKeyword);

    // 해당 카테고리에서 가장 작은 quizId 찾기 (즉, 첫 번째 퀴즈)
    @Query("SELECT MIN(q.quizId) FROM Quiz q WHERE q.category = :category")
    Long findFirstQuizIdByCategory(@Param("category") String category);

    // 현재 풀려는 퀴즈보다 작은 ID 중에서 가장 큰 ID(즉, 바로 이전 퀴즈) 찾기
    @Query("SELECT MAX(q.quizId) FROM Quiz q WHERE q.category = :category AND q.quizId < :quizId")
    Long findPreviousQuizId(@Param("category") String category, @Param("quizId") Long quizId);
}
