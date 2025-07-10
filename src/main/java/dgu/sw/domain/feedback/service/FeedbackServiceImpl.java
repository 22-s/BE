package dgu.sw.domain.feedback.service;

import dgu.sw.domain.feedback.converter.FeedbackConverter;
import dgu.sw.domain.feedback.dto.FeedbackRequestDto;
import dgu.sw.domain.feedback.dto.FeedbackResponseDto;
import dgu.sw.domain.feedback.entity.Feedback;
import dgu.sw.domain.feedback.repository.FeedbackRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.FeedbackException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Override
    public FeedbackResponseDto createFeedback(Long userId, FeedbackRequestDto dto) {
        if (dto.getCategory() == null ||
                dto.getContent() == null ||
                dto.getIsAnonymous() == null) {
            throw new FeedbackException(ErrorStatus.FEEDBACK_BAD_REQUEST);
        }

        boolean validCategory = false;
        for (var category : dgu.sw.domain.feedback.dto.FeedbackCategory.values()) {
            if (category == dto.getCategory()) {
                validCategory = true;
                break;
            }
        }

        if (!validCategory) {
            throw new FeedbackException(ErrorStatus.FEEDBACK_INVALID_CATEGORY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Feedback feedback = FeedbackConverter.toEntity(dto, user);
        Feedback saved = feedbackRepository.save(feedback);

        return FeedbackConverter.toResponseDto(saved);
    }
}