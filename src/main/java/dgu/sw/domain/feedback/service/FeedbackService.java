package dgu.sw.domain.feedback.service;

import dgu.sw.domain.feedback.dto.FeedbackRequestDto;
import dgu.sw.domain.feedback.dto.FeedbackResponseDto;

public interface FeedbackService {

    FeedbackResponseDto createFeedback(Long userId, FeedbackRequestDto dto);
}