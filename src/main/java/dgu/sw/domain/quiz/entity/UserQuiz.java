package dgu.sw.domain.quiz.entity;

import dgu.sw.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "userQuiz")
public class UserQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userQuizId;

    private boolean isLocked;
    private boolean isCorrect;
    private boolean isReviewed;

    @Temporal(TemporalType.DATE)
    private LocalDate solvedDate;

    private Boolean retriedToday; // 오늘 다시 풀었는지 여부

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    public void updateCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
