package dgu.sw.domain.quiz.generator;

import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RandomQuizListGenerator implements QuizListGenerator {
    private final QuizRepository quizRepository;

    @Override
    public List<Quiz> generateQuizList() {
        //랜덤한 10문제 리턴
        return quizRepository.findRandomQuizzes(10);
    }
}
