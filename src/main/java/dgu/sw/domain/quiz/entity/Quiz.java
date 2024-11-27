package dgu.sw.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String answer;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String questionDetail;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<UserQuiz> userQuizzes;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizReviewList> quizReviewLists;
}
