package dgu.sw.domain.user.repository;

import dgu.sw.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUserId(Long userId);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
