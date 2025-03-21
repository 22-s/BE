package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;

public interface MockTestService {
    CreateMockTestResponse startMockTest(String userId);
    CreateMockTestResponse submitMockTest(Long mockTestId, SubmitMockTestRequest request);
}
