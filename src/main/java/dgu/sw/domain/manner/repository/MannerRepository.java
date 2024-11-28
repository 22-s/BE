package dgu.sw.domain.manner.repository;

import dgu.sw.domain.manner.entity.Manner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MannerRepository extends JpaRepository<Manner, Long> {
    List<Manner> findByCategory(String category);

    List<Manner> findByCategoryContainingOrTitleContainingOrContentContaining(
            String categoryKeyword,
            String titleKeyword,
            String contentKeyword
    );

    List<Manner> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String keyword1, String category2, String keyword2);
}