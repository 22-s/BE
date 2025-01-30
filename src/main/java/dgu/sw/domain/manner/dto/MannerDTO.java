package dgu.sw.domain.manner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MannerDTO {

        public static class MannerResponse {
            @Getter
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class MannerListResponse {
                private Long mannerId;
                private String category;
                private String title;
                private String content;
                private String imageUrl;
                private boolean isFavorited;
            }

            @Getter
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class MannerDetailResponse {
                private Long mannerId;
                private String category;
                private String title;
                private String content;
                private String imageUrl;
                private boolean isFavorited;
            }

            @Getter
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class MannerFavoriteResponse {
                private Long mannerId;
                private String category;
                private String title;
                private String contentPreview;
                private String imageUrl;
            }

        }
}
