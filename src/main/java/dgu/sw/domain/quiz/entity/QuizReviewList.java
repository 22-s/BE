package dgu.sw.domain.quiz.entity;

import dgu.sw.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "quizReviewList")
public class QuizReviewList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizReviewId;

    @ManyToOne
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
