package dgu.sw.domain.manner.entity;

import dgu.sw.domain.user.entity.User;
import dgu.sw.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "favoriteManner")
public class FavoriteManner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteMannerId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mannerId")
    private Manner manner;

    private String category;
    private String content;
}