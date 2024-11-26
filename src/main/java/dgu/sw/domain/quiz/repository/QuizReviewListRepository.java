package dgu.sw.domain.quiz.repository;

import dgu.sw.domain.quiz.entity.QuizReviewList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizReviewListRepository extends JpaRepository<QuizReviewList, Long> {
    List<QuizReviewList> findByUser_UserId(Long userId);
}
