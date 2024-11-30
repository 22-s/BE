package dgu.sw.domain.voca.service;

import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaListResponse;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaFavoriteResponse;

import java.util.List;

public interface VocaService {
    List<VocaListResponse> getVocaList(String userId, String category);

    List<VocaListResponse> searchVoca(String userId, String keyword);

    void addFavorite(String userId, Long vocaId);

    void removeFavorite(String userId, Long vocaId);

    List<VocaFavoriteResponse> getFavorites(String userId);
}
