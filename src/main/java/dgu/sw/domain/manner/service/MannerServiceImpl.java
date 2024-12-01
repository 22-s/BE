package dgu.sw.domain.manner.service;

import dgu.sw.domain.manner.converter.MannerConverter;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;
import dgu.sw.domain.manner.entity.FavoriteManner;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.manner.repository.FavoriteMannerRepository;
import dgu.sw.domain.manner.repository.MannerRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.MannerException;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MannerServiceImpl implements MannerService {

    private final MannerRepository mannerRepository;
    private final FavoriteMannerRepository favoriteMannerRepository;
    private final UserRepository userRepository;

    @Override
    public List<String> getCategories() {
        return mannerRepository.findAll()
                .stream()
                .map(Manner::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<MannerListResponse> getMannersByCategory(String category, String userId) {
        return mannerRepository.findByCategory(category)
                .stream()
                .map(manner -> {
                    boolean isFavorited = false;
                    if (userId != null) {
                        User user = userRepository.findById(Long.valueOf(userId)).orElse(null);
                        isFavorited = user != null && favoriteMannerRepository.existsByUserAndManner(user, manner);
                    }
                    return MannerConverter.toMannerListResponse(manner, isFavorited);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MannerDetailResponse getMannerDetail(Long mannerId, String userId) {
        Manner manner = mannerRepository.findById(mannerId)
                .orElseThrow(() -> new MannerException(ErrorStatus.MANNER_NOT_FOUND));

        boolean isFavorited = false;
        if (userId != null) {
            User user = userRepository.findById(Long.valueOf(userId)).orElse(null);
            isFavorited = user != null && favoriteMannerRepository.existsByUserAndManner(user, manner);
        }

        return MannerConverter.toMannerDetailResponse(manner, isFavorited);
    }

    @Override
    public List<MannerListResponse> searchManners(String keyword) {
        return mannerRepository.findByCategoryContainingOrTitleContainingOrContentContaining(keyword, keyword, keyword)
                .stream()
                .map(manner -> MannerConverter.toMannerListResponse(manner, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<MannerListResponse> searchMannersByCategory(String category, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return mannerRepository.findByCategory(category)
                    .stream()
                    .map(manner -> MannerConverter.toMannerListResponse(manner, false)) // 로그인 없이 항상 false
                    .collect(Collectors.toList());
        }
        return mannerRepository.findByCategoryAndTitleContainingOrCategoryAndContentContaining(
                        category, keyword, category, keyword)
                .stream()
                .map(manner -> MannerConverter.toMannerListResponse(manner, false)) // 로그인 없이 항상 false
                .collect(Collectors.toList());
    }

    @Override
    public void addFavorite(String userId, Long mannerId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        Manner manner = mannerRepository.findById(mannerId)
                .orElseThrow(() -> new MannerException(ErrorStatus.MANNER_NOT_FOUND));

        boolean isAlreadyFavorite = favoriteMannerRepository.existsByUserAndManner(user, manner);
        if (isAlreadyFavorite) {
            throw new MannerException(ErrorStatus.MANNER_ALREADY_FAVORITED);
        }

        FavoriteManner favoriteManner = FavoriteManner.builder()
                .user(user)
                .manner(manner)
                .build();
        favoriteMannerRepository.save(favoriteManner);
    }

    @Override
    public void removeFavorite(String userId, Long mannerId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        Manner manner = mannerRepository.findById(mannerId)
                .orElseThrow(() -> new MannerException(ErrorStatus.MANNER_NOT_FOUND));

        FavoriteManner favoriteManner = favoriteMannerRepository.findByUserAndManner(user, manner)
                .orElseThrow(() -> new MannerException(ErrorStatus.MANNER_FAVORITE_NOT_FOUND));

        favoriteMannerRepository.delete(favoriteManner);
    }

    @Override
    public List<MannerFavoriteResponse> getFavorites(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        return favoriteMannerRepository.findByUser(user)
                .stream()
                .map(favoriteManner -> MannerConverter.toMannerFavoriteResponse(favoriteManner.getManner()))
                .collect(Collectors.toList());
    }
}