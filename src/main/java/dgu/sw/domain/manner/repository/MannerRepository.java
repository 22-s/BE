package dgu.sw.domain.manner.repository;

import dgu.sw.domain.manner.entity.Manner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MannerRepository extends JpaRepository<Manner, Long> {
    List<Manner> findByCategory(String category);
}