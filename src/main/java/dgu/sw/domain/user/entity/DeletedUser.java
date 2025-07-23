package dgu.sw.domain.user.entity;

import dgu.sw.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "deletedUser")
public class DeletedUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;            // 원래 사용자 ID (참조용)

    private String email;
    private String nickname;
    private LocalDate joinDate;

    private LocalDate deletedAt;
}
