package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.QuizReviewList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizReviewListRepository extends JpaRepository<QuizReviewList, Long> {
}
