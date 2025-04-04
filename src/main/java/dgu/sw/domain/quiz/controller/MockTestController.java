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

import java.util.List;

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

    @GetMapping("/result/all")
    @Operation(summary = "전체 모의고사 결과 조회", description = "해당 사용자가 완료한 모든 모의고사 결과를 반환합니다.")
    public ApiResponse<List<MockTestResultResponse>> getAllMockTestResults(Authentication authentication) {
        List<MockTestResultResponse> response = mockTestService.getAllMockTestResults(authentication.getName());
        return ApiResponse.onSuccess(response);
    }
}
