package dgu.sw.domain.user.entity;

import dgu.sw.domain.manner.entity.FavoriteManner;
import dgu.sw.domain.quiz.entity.QuizReviewList;
import dgu.sw.domain.quiz.entity.UserQuiz;
import dgu.sw.domain.voca.entity.FavoriteVoca;
import dgu.sw.global.security.OAuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String nickname;
    private String email;
    private String password;
    private LocalDate joinDate;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserQuiz> userQuizzes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteManner> favoriteMannerGuides;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteVoca> favoriteVocabularies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuizReviewList> quizReviewLists;

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateJoinDate(LocalDate newDate) {
        this.joinDate = newDate;
    }
}
