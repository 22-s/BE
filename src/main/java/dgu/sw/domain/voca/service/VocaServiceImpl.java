package dgu.sw.domain.voca.service;

import dgu.sw.domain.voca.converter.VocaConverter;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaListResponse;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaFavoriteResponse;
import dgu.sw.domain.voca.entity.FavoriteVoca;
import dgu.sw.domain.voca.entity.Voca;
import dgu.sw.domain.voca.repository.FavoriteVocaRepository;
import dgu.sw.domain.voca.repository.VocaRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.VocaException;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocaServiceImpl implements VocaService {

    private final VocaRepository vocaRepository;
    private final FavoriteVocaRepository favoriteVocaRepository;
    private final UserRepository userRepository;

    @Override
    public List<VocaListResponse> getVocaList(String category) {
        List<Voca> vocas = vocaRepository.findByCategory(category);
        if (vocas.isEmpty()) {
            throw new VocaException(ErrorStatus.VOCA_LIST_NOT_FOUND);
        }
        return vocas.stream()
                .map(VocaConverter::toVocaListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VocaListResponse> searchVoca(String keyword) {
        List<Voca> vocas = vocaRepository.findByTermContainingOrDescriptionContaining(keyword, keyword);
        if (vocas.isEmpty()) {
            throw new VocaException(ErrorStatus.VOCA_SEARCH_NO_RESULTS);
        }
        return vocas.stream()
                .map(VocaConverter::toVocaListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addFavorite(String userId, Long vocaId) {
        User user = userRepository.findByUserId(Long.valueOf(userId));
        Voca voca = vocaRepository.findById(vocaId)
                .orElseThrow(() -> new VocaException(ErrorStatus.VOCA_NOT_FOUND));

        if (favoriteVocaRepository.existsByUserAndVoca(user, voca)) {
            throw new VocaException(ErrorStatus.VOCA_ALREADY_FAVORITED);
        }

        FavoriteVoca favorite = FavoriteVoca.builder().user(user).voca(voca).build();
        favoriteVocaRepository.save(favorite);
    }

    @Override
    public void removeFavorite(String userId, Long vocaId) {
        User user = userRepository.findByUserId(Long.valueOf(userId));
        Voca voca = vocaRepository.findById(vocaId)
                .orElseThrow(() -> new VocaException(ErrorStatus.VOCA_NOT_FOUND));

        FavoriteVoca favorite = favoriteVocaRepository.findByUserAndVoca(user, voca)
                .orElseThrow(() -> new VocaException(ErrorStatus.VOCA_FAVORITE_NOT_FOUND));

        favoriteVocaRepository.delete(favorite);
    }

    @Override
    public List<VocaFavoriteResponse> getFavorites(String userId) {
        User user = userRepository.findByUserId(Long.valueOf(userId));
        List<FavoriteVoca> favorites = favoriteVocaRepository.findByUser(user);
        return favorites.stream()
                .map(favorite -> VocaConverter.toVocaFavoriteResponse(favorite.getVoca()))
                .collect(Collectors.toList());
    }
}
