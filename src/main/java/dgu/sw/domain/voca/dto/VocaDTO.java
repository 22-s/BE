package dgu.sw.domain.voca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VocaDTO {

    public static class VocaResponse {

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VocaListResponse {
            private Long vocaId;
            private String category;
            private String term;
            private String description;
            private String example;
        }

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VocaFavoriteResponse {
            private Long vocaId;
            private String term;
            private String description;
            private String example;
        }
    }
}
