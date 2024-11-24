package dgu.sw.domain.manner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MannerListDTO {
    private String category;
    private String title;
    private String contentPreview; // 내용 20자로 제한
    private String imageUrl;
}