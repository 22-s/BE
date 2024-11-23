package dgu.sw.domain.manner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "manner")
public class Manner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mannerId;

    private String category;
    private String title;
    private String content;
    private String imageUrl;

    @OneToMany(mappedBy = "manner", cascade = CascadeType.ALL)
    private List<FavoriteManner> favoriteManner;
}
