package dgu.sw.domain.quiz.entity;

import dgu.sw.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "mockTestQuiz")
public class MockTestQuiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mockTestQuizId;

    @ManyToOne
    @JoinColumn(name = "mockTestId")
    private MockTest mockTest;

    @ManyToOne
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    private boolean isCorrect;

    public void updateCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}

