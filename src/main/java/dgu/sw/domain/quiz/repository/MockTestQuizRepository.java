package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.MockTestQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestQuizRepository extends JpaRepository<MockTestQuiz, Long> {

    List<MockTestQuiz> findByMockTest_MockTestId(Long mockTestId);
}

