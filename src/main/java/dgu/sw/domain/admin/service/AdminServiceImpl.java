package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminVocaResponse;
import dgu.sw.domain.admin.converter.AdminConverter;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminQuizResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.manner.repository.MannerRepository;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.domain.voca.repository.VocaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final MannerRepository mannerRepository;
    private final QuizRepository quizRepository;
    private final VocaRepository vocaRepository;

    @Override
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminConverter::toAdminUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMannerResponse> getAllManners() {
        return mannerRepository.findAll().stream()
                .map(AdminConverter::toAdminMannerResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteManner(Long mannerId) {
        mannerRepository.deleteById(mannerId);
    }

    @Override
    @Transactional
    public void saveManner(AdminMannerRequest request) {
        Manner manner = AdminConverter.toManner(request);
        mannerRepository.save(manner);
    }

    @Override
    public List<AdminQuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(AdminConverter::toAdminQuizResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveQuiz(AdminQuizRequest request) {
        Quiz quiz = AdminConverter.toQuiz(request);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    @Override
    public List<AdminVocaResponse> getAllVocas() {
        return vocaRepository.findAll().stream()
                .map(AdminConverter::toAdminVocaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveVoca(AdminVocaRequest request) {
        vocaRepository.save(AdminConverter.toVoca(request));
    }

    @Override
    @Transactional
    public void deleteVoca(Long vocaId) {
        vocaRepository.deleteById(vocaId);
    }
}
