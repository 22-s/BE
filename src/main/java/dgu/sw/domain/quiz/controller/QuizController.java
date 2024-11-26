package dgu.sw.domain.quiz.controller;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.service.QuizService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
@Tag(name = "Quiz 컨트롤러", description = "퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "퀴즈 목록 조회", description = "카테고리별 퀴즈 리스트를 반환합니다.")
    public ApiResponse<List<QuizListResponse>> getQuizList(
            Authentication authentication,
            @RequestParam String category) {
        List<QuizListResponse> response = quizService.getQuizList(authentication.getName(), category);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "퀴즈 상세 조회", description = "선택한 퀴즈의 상세 정보를 반환합니다.")
    public ApiResponse<QuizDetailResponse> getQuizDetail(
            Authentication authentication,
            @PathVariable Long quizId) {
        QuizDetailResponse response = quizService.getQuizDetail(authentication.getName(), quizId);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/{quizId}/submit")
    @Operation(summary = "퀴즈 답안 제출", description = "사용자가 제출한 답안을 처리하고 정답 여부를 반환합니다.")
    public ApiResponse<QuizResultResponse> submitQuizAnswer(
            Authentication authentication,
            @PathVariable Long quizId,
            @RequestBody SubmitQuizRequest quizAnswerDto) {
        QuizResultResponse response = quizService.submitQuizAnswer(authentication.getName(), quizId, quizAnswerDto);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/{quizId}/review")
    @Operation(summary = "복습 추가", description = "틀린 문제를 복습 리스트에 추가합니다.")
    public ApiResponse<String> addQuizToReview(
            Authentication authentication,
            @PathVariable Long quizId) {
        quizService.addQuizToReview(authentication.getName(), quizId);
        return ApiResponse.onSuccess("복습 리스트에 추가되었습니다.");
    }

    @GetMapping("/review")
    @Operation(summary = "복습 리스트 조회", description = "사용자의 복습 리스트를 반환합니다.")
    public ApiResponse<List<QuizDetailResponse>> getReviewList(Authentication authentication) {
        List<QuizDetailResponse> response = quizService.getReviewList(authentication.getName());
        return ApiResponse.onSuccess(response);
    }


    @GetMapping("/search")
    @Operation(summary = "퀴즈 검색", description = "키워드와 카테고리를 기반으로 퀴즈를 검색합니다.")
    public ApiResponse<List<QuizListResponse>> searchQuizzes(
            @RequestParam(required = false) String keyword) {
        List<QuizListResponse> response = quizService.searchQuizzes(keyword);
        return ApiResponse.onSuccess(response);
    }
}