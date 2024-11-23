package dgu.sw.domain.voca.entity;

import dgu.sw.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "favoriteVoca")
public class FavoriteVoca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteVocabId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vocaId")
    private Voca voca;
}
