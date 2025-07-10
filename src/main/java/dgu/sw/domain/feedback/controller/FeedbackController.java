package dgu.sw.domain.feedback.controller;

import dgu.sw.domain.feedback.dto.FeedbackRequestDto;
import dgu.sw.domain.feedback.dto.FeedbackResponseDto;
import dgu.sw.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import dgu.sw.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FeedbackRequestDto dto
    ) {
        Long userId = userDetails.getId();
        FeedbackResponseDto responseDto = feedbackService.createFeedback(userId, dto);
        return ResponseEntity.ok(responseDto);
    }
}
