package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.MockTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {
    MockTest findTopByUser_UserIdOrderByCreatedDateDesc(Long userId); // ✅
    @Query("SELECT COUNT(me) FROM MockTest me WHERE me.isCompleted = true")
    long countCompletedExams();

    @Query("SELECT COUNT(me) FROM MockTest me WHERE me.correctCount > :score")
    long countByCorrectCountGreaterThan(@Param("score") int score);

}
