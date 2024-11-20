package dgu.sw.domain.voca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "voca")
public class Voca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vocaId;

    private String category;
    private String term;
    private String description;
    private String example;

    @OneToMany(mappedBy = "voca", cascade = CascadeType.ALL)
    private List<FavoriteVoca> favoriteVocas;
}
