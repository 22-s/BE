package dgu.sw.domain.manner.service;

import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;

import java.util.List;

public interface MannerService {
    List<String> getCategories();
    List<MannerListResponse> getMannersByCategory(int category, String userId);
    MannerDetailResponse getMannerDetail(Long mannerId, String userId);
    List<MannerListResponse> searchManners(String keyword);
    List<MannerListResponse> searchMannersByCategory(String category, String keyword);
    void addFavorite(String userId, Long mannerId);
    void removeFavorite(String userId, Long mannerId);
    List<MannerFavoriteResponse> getFavorites(String userId);
}
