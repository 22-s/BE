package dgu.sw.domain.manner.repository;

import dgu.sw.domain.manner.entity.Manner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MannerRepository extends JpaRepository<Manner, Long> {
    List<Manner> findByCategory(String category);

    List<Manner> findByCategoryContainingOrTitleContainingOrContentContaining(
            String categoryKeyword,
            String titleKeyword,
            String contentKeyword
    );

    @Query("SELECT m FROM Manner m WHERE m.category = :categoryName AND (m.title LIKE %:keyword% OR m.content LIKE %:keyword%)")
    List<Manner> searchByCategoryAndKeyword(@Param("categoryName") String categoryName, @Param("keyword") String keyword);
}