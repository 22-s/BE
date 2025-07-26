package dgu.sw.domain.quiz.controller;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.*;
import dgu.sw.domain.quiz.service.QuizService;
import dgu.sw.global.ApiResponse;
import dgu.sw.global.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
            @LoginUser Long userId,
            @RequestParam int category) {
        List<QuizListResponse> response = quizService.getQuizList(userId, category);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "퀴즈 상세 조회", description = "선택한 퀴즈의 상세 정보를 반환합니다.")
    public ApiResponse<QuizDetailResponse> getQuizDetail(
            @LoginUser Long userId,
            @PathVariable Long quizId) {
        QuizDetailResponse response = quizService.getQuizDetail(userId, quizId);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/{quizId}/submit")
    @Operation(summary = "퀴즈 답안 제출", description = "사용자가 제출한 답안을 처리하고 정답 여부를 반환합니다.")
    public ApiResponse<QuizResultResponse> submitQuizAnswer(
            @LoginUser Long userId,
            @PathVariable Long quizId,
            @RequestBody SubmitQuizRequest quizAnswerDto) {
        QuizResultResponse response = quizService.submitQuizAnswer(userId, quizId, quizAnswerDto);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/{quizId}/review")
    @Operation(summary = "복습 추가", description = "틀린 문제를 복습 리스트에 추가합니다.")
    public ApiResponse<String> addQuizToReview(
            @LoginUser Long userId,
            @PathVariable Long quizId) {
        quizService.addQuizToReview(userId, quizId);
        return ApiResponse.onSuccess("복습 리스트에 추가되었습니다.");
    }

    @GetMapping("/review")
    @Operation(summary = "복습 리스트 조회", description = "사용자의 복습 리스트를 반환합니다.")
    public ApiResponse<List<QuizReviewResponse>> getReviewList(@LoginUser Long userId) {
        List<QuizReviewResponse> response = quizService.getReviewList(userId);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{quizId}/review")
    @Operation(summary = "복습 리스트 삭제", description = "복습 리스트에서 특정 퀴즈를 삭제합니다.")
    public ApiResponse<String> removeQuizFromReview(
            @LoginUser Long userId,
            @PathVariable Long quizId) {
        quizService.removeQuizFromReview(userId, quizId);
        return ApiResponse.onSuccess("복습 리스트에서 삭제되었습니다.");
    }

    @GetMapping("/search")
    @Operation(summary = "퀴즈 검색", description = "키워드와 카테고리를 기반으로 퀴즈를 검색합니다.")
    public ApiResponse<List<QuizSearchResponse>> searchQuizzes(
            @LoginUser Long userId,
            @RequestParam(required = false) String keyword) {
        List<QuizSearchResponse> response = quizService.searchQuizzes(userId, keyword);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/main")
    @Operation(summary = "퀴즈 메인 정보 조회", description = "어제 푼 퀴즈 수, 진척도, 모의고사 점수, Top5 오답 퀴즈 등 메인 페이지 데이터를 반환합니다.")
    public ApiResponse<QuizMainPageResponse> getQuizMainInfo(@LoginUser Long userId) {
        QuizMainPageResponse response = quizService.getQuizMainPageData(userId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/yesterday")
    @Operation(summary = "어제 푼 퀴즈 조회", description = "어제 푼 퀴즈 목록을 반환합니다.")
    public ApiResponse<List<YesterdayQuizResponse>> getYesterdayQuizzes(@LoginUser Long userId) {
        List<YesterdayQuizResponse> response = quizService.getYesterdaySolvedQuizzes(userId);
        return ApiResponse.onSuccess(response);
    }
}