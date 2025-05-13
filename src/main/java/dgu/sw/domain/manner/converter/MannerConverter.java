package dgu.sw.domain.manner.converter;

import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.entity.Manner;

public class MannerConverter {
    public static MannerListResponse toMannerListResponse(Manner manner, boolean isFavorited) {
        return MannerListResponse.builder()
                .mannerId(manner.getMannerId())
                .category(manner.getCategory())
                .title(manner.getTitle())
                .content(manner.getContent())
                .imageUrl(manner.getImageUrl())
                .isFavorited(isFavorited)
                .build();
    }

    public static MannerDetailResponse toMannerDetailResponse(Manner manner, boolean isFavorited) {
        return MannerDetailResponse.builder()
                .mannerId(manner.getMannerId())
                .category(manner.getCategory())
                .title(manner.getTitle())
                .content(manner.getContent())
                .imageUrl(manner.getImageUrl())
                .isFavorited(isFavorited)
                .build();
    }

    public static MannerFavoriteResponse toMannerFavoriteResponse(Manner manner) {
        return MannerFavoriteResponse.builder()
                .mannerId(manner.getMannerId())
                .title(manner.getTitle())
                .category(manner.getCategory())
                .content(manner.getContent())
                .imageUrl(manner.getImageUrl())
                .build();

    }
}
