package dgu.sw.domain.voca.repository;

import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.voca.entity.FavoriteVoca;
import dgu.sw.domain.voca.entity.Voca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteVocaRepository extends JpaRepository<FavoriteVoca, Long> {
    boolean existsByUserAndVoca(User user, Voca voca);

    Optional<FavoriteVoca> findByUserAndVoca(User user, Voca voca);

    List<FavoriteVoca> findByUser(User user);
}