package dgu.sw.domain.voca.repository;

import dgu.sw.domain.voca.entity.Voca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocaRepository extends JpaRepository<Voca, Long> {
    List<Voca> findByCategory(String category);
    List<Voca> findByTermContainingOrDescriptionContaining(String term, String description);

}
