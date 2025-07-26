package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmitMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;

import java.util.List;

public interface MockTestService {
    CreateMockTestResponse startMockTest(Long userId);
    SubmitMockTestResponse submitMockTest(Long mockTestId, SubmitMockTestRequest request);
    List<MockTestResultResponse> getAllMockTestResults(Long userId);
}
