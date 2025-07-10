package dgu.sw.domain.feedback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDto {
    private FeedbackCategory category;
    private String content;
    private Boolean isAnonymous;
}