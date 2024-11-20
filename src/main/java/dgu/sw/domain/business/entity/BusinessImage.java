package dgu.sw.domain.business.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "businessImage")
public class BusinessImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessImageId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "businessId")
    private Business business;
}
