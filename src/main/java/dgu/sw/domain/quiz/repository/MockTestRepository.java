package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {
    @Query("SELECT COUNT(me) FROM MockTest me WHERE me.isCompleted = true")
    long countCompletedExams();

    @Query("SELECT COUNT(me) FROM MockTest me WHERE me.correctCount > :score")
    long countByCorrectCountGreaterThan(@Param("score") int score);

    Optional<MockTest> findTopByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<MockTest> findAllByIsCompletedTrue();

    Optional<MockTest> findTopByUser_UserIdAndMockTestIdLessThanOrderByMockTestIdDesc(Long userId, Long mockTestId);

    List<MockTest> findByUser_UserIdAndMockTestIdLessThan(Long userId, Long mockTestId);

    List<MockTest> findByUser_UserIdAndIsCompletedTrueOrderByMockTestIdAsc(Long userId);
}
