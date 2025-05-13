package dgu.sw.domain.quiz.entity;

import dgu.sw.domain.user.entity.User;
import dgu.sw.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "mockTest")
public class MockTest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mockTestId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private boolean isCompleted; // 다 풀었는지 여부

    private int correctCount;

    private double topPercentile;

    @OneToMany(mappedBy = "mockTest", cascade = CascadeType.ALL)
    private List<MockTestQuiz> mockTestQuizzes;

    public void updateCompleted(boolean isCorrect, int correctCount, double topPercentile) {
        this.isCompleted = isCorrect;
        this.correctCount = correctCount;
        this.topPercentile = topPercentile;
    }
}

