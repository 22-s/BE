package dgu.sw.domain.manner.repository;

import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.manner.entity.FavoriteManner;
import dgu.sw.domain.manner.entity.Manner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMannerRepository extends JpaRepository<FavoriteManner, Long> {
    boolean existsByUserAndManner(User user, Manner manner);

    Optional<FavoriteManner> findByUserAndManner(User user, Manner manner);

    List<FavoriteManner> findByUser(User user);
}