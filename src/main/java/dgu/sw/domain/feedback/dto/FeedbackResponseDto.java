package dgu.sw.domain.feedback.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackResponseDto {
    private Long feedbackId;
    private String nickname;
    private FeedbackCategory category;
    private String content;
    private Boolean isAnonymous;
}