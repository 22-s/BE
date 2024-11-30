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
    public List<VocaListResponse> getVocaList(String userId, String category) {
        List<Voca> vocas = vocaRepository.findByCategory(category);

        if (vocas.isEmpty()) {
            throw new VocaException(ErrorStatus.VOCA_LIST_NOT_FOUND);
        }

        // 사용자의 즐겨찾기 가져오기
        User user = userRepository.findByUserId(Long.valueOf(userId));
        List<Long> favoritedVocaIds = favoriteVocaRepository.findByUser(user).stream()
                .map(favorite -> favorite.getVoca().getVocaId())
                .collect(Collectors.toList());

        // 업무 용어 리스트와 즐겨찾기 여부 매핑
        return vocas.stream()
                .map(voca -> VocaConverter.toVocaListResponse(voca, favoritedVocaIds.contains(voca.getVocaId())))
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
