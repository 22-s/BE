package dgu.sw.domain.manner.service;

import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;

import java.util.List;

public interface MannerService {
    List<String> getCategories();
    List<MannerListResponse> getMannersByCategory(String category);
    MannerDetailResponse getMannerDetail(Long mannerId);
    List<MannerListResponse> searchManners(String keyword);
    List<MannerListResponse> searchMannersByCategory(String category, String keyword);
    void addFavorite(String userId, Long mannerId);
    void removeFavorite(String userId, Long mannerId);
    List<MannerFavoriteResponse> getFavorites(String userId);
}
