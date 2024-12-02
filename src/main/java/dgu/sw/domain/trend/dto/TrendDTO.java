package dgu.sw.domain.trend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendDTO {
    private String id;
    private String category;
    private String title;
    private String content;
    private String date;
    private String source;
    private String imageUrl;
    private String authorProfile;
}
