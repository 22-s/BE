package dgu.sw.domain.quiz.controller;

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
}
