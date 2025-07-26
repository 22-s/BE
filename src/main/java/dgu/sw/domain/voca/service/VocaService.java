package dgu.sw.domain.voca.service;

import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaListResponse;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaFavoriteResponse;

import java.util.List;

public interface VocaService {
    List<VocaListResponse> getVocaList(Long userId, String category);

    List<VocaListResponse> searchVoca(Long userId, String keyword);

    void addFavorite(Long userId, Long vocaId);

    void removeFavorite(Long userId, Long vocaId);

    List<VocaFavoriteResponse> getFavorites(Long userId);
}
