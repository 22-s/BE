package dgu.sw.domain.quiz.generator;

import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LevelBasedQuizListGenerator implements QuizListGenerator {
    private final QuizRepository quizRepository;

    @Override
    public List<Quiz> generateQuizList() {
        //모의고사 총 문제 수
        final int TEST_SIZE = 20;

        //난이도별 문제 수
        int easyCount = Math.round(TEST_SIZE * (float)0.4);
        int mediumCount = Math.round(TEST_SIZE * (float)0.4);
        int hardCount = TEST_SIZE - easyCount - mediumCount;

        //문제 추출 및
        return Stream.of(
                quizRepository.findRandomQuizzesByLevel(easyCount, "EASY"),
                quizRepository.findRandomQuizzesByLevel(mediumCount, "MEDIUM"),
                quizRepository.findRandomQuizzesByLevel(hardCount, "HARD")
        )
                .flatMap(List::stream)
                .toList();
    }
}
