package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;

public interface MockTestService {
    CreateMockTestResponse startMockTest(String userId);
}
