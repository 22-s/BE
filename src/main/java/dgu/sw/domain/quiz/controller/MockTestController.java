package dgu.sw.domain.quiz.controller;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmitMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;
import dgu.sw.domain.quiz.service.MockTestService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mockTest")
@Tag(name = "mockTest 컨트롤러", description = "모의고사 관련 API")
public class MockTestController {

    private final MockTestService mockTestService;

    @PostMapping("/start")
    @Operation(summary = "모의고사 시작", description = "랜덤한 10개의 퀴즈로 모의고사를 시작합니다.")
    public ApiResponse<CreateMockTestResponse> startMockTest(Authentication authentication) {
        CreateMockTestResponse response = mockTestService.startMockTest(authentication.getName());
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/{mockTestId}/submit")
    @Operation(summary = "모의고사 제출", description = "사용자가 선택한 답안을 제출하고 결과를 반환합니다.")
    public ApiResponse<SubmitMockTestResponse> submitMockTest(
            @PathVariable Long mockTestId,
            @RequestBody SubmitMockTestRequest request) {
        SubmitMockTestResponse response = mockTestService.submitMockTest(mockTestId, request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{mockTestId}/result")
    @Operation(summary = "모의고사 결과 조회", description = "모의고사 결과를 반환합니다.")
    public ApiResponse<MockTestResultResponse> getMockTestResult(@PathVariable Long mockTestId) {
        MockTestResultResponse response = mockTestService.getMockTestResult(mockTestId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/previous/result")
    @Operation(summary = "이전 모의고사 결과 조회", description = "가장 최근에 완료한 이전 모의고사 결과를 조회합니다.")
    public ApiResponse<MockTestResultResponse> getPreviousMockTestResult(Authentication authentication) {
        MockTestResultResponse response = mockTestService.getPreviousMockTestResult(authentication.getName());
        return ApiResponse.onSuccess(response);
    }
}
