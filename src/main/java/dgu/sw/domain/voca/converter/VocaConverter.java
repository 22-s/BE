package dgu.sw.domain.voca.converter;

import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaListResponse;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaFavoriteResponse;
import dgu.sw.domain.voca.entity.Voca;

public class VocaConverter {
    public static VocaListResponse toVocaListResponse(Voca voca, boolean isFavorited) {
        return VocaListResponse.builder()
                .vocaId(voca.getVocaId())
                .category(voca.getCategory())
                .term(voca.getTerm())
                .description(voca.getDescription())
                .example(voca.getExample())
                .isFavorited(isFavorited)
                .build();
    }

    public static VocaFavoriteResponse toVocaFavoriteResponse(Voca voca) {
        return VocaFavoriteResponse.builder()
                .vocaId(voca.getVocaId())
                .term(voca.getTerm())
                .description(voca.getDescription())
                .example(voca.getExample())
                .build();
    }
}
