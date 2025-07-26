package dgu.sw.domain.manner.service;

import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;

import java.util.List;

public interface MannerService {
    List<MannerListResponse> getMannersByCategory(int category, Long userId);
    MannerDetailResponse getMannerDetail(Long mannerId, Long userId);
    List<MannerListResponse> searchManners(String keyword);
    List<MannerListResponse> searchMannersByCategory(int category, String keyword);
    void addFavorite(Long userId, Long mannerId);
    void removeFavorite(Long userId, Long mannerId);
    List<MannerFavoriteResponse> getFavorites(Long userId);
}
