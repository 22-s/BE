package dgu.sw.domain.feedback.converter;

import dgu.sw.domain.feedback.dto.FeedbackRequestDto;
import dgu.sw.domain.feedback.dto.FeedbackResponseDto;
import dgu.sw.domain.feedback.entity.Feedback;
import dgu.sw.domain.user.entity.User;

public class FeedbackConverter {

    public static Feedback toEntity(FeedbackRequestDto dto, User user) {
        return Feedback.builder()
                .user(user)
                .category(dto.getCategory())
                .content(dto.getContent())
                .isAnonymous(dto.getIsAnonymous())
                .build();
    }

    public static FeedbackResponseDto toResponseDto(Feedback feedback) {
        String nickname = feedback.getIsAnonymous() ? "익명" : feedback.getUser().getNickname();
        return FeedbackResponseDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .nickname(nickname)
                .category(feedback.getCategory())
                .content(feedback.getContent())
                .isAnonymous(feedback.getIsAnonymous())
                .build();
    }
}