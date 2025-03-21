package dgu.sw.domain.quiz.entity;

import dgu.sw.domain.user.entity.User;
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
public class MockTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mockTestId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private LocalDate createdDate;

    private boolean isCompleted; // 다 풀었는지 여부

    private int correctCount;

    @OneToMany(mappedBy = "mockTest", cascade = CascadeType.ALL)
    private List<MockTestQuiz> mockTestQuizzes;
}

