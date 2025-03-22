package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmitMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;

public interface MockTestService {
    CreateMockTestResponse startMockTest(String userId);
    SubmitMockTestResponse submitMockTest(Long mockTestId, SubmitMockTestRequest request);
    MockTestResultResponse getMockTestResult(Long mockTestId);
}
