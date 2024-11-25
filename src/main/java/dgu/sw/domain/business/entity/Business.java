package dgu.sw.domain.business.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "business")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessId;

    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDateTime publishedDate;
    private String category;
    private String source;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    private List<BusinessImage> businessImages;
}
