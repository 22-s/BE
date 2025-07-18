package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.converter.AdminConverter;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminLoginRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.*;
import dgu.sw.domain.feedback.repository.FeedbackRepository;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.manner.repository.MannerRepository;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.user.entity.Role;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.domain.voca.entity.Voca;
import dgu.sw.domain.voca.repository.VocaRepository;
import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.exception.MannerException;
import dgu.sw.global.exception.QuizException;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.exception.VocaException;
import dgu.sw.global.security.JwtTokenProvider;
import dgu.sw.global.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final FeedbackRepository feedbackRepository;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        User admin = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 관리자 권한 확인
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UserException(ErrorStatus.FORBIDDEN_ACCESS);
        }

        // 비밀번호 일치 확인 (bcrypt 비교)
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UserException(ErrorStatus.INVALID_CREDENTIALS);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(admin);
        String refreshToken = jwtTokenProvider.generateRefreshToken(admin);

        // Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(admin.getUserId().toString(), refreshToken);

        return AdminConverter.toAdminLoginResponse(accessToken, refreshToken, admin);
    }

    private void checkAdminRole(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 사용자 권한 확인
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserException(ErrorStatus.FORBIDDEN_ACCESS);
        }
    }

    @Override
    public List<AdminUserResponse> getAllUsers(String userId) {
        checkAdminRole(userId);  // 권한 확인
        return userRepository.findAll().stream()
                .map(AdminConverter::toAdminUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMannerResponse> getAllManners(String userId) {
        checkAdminRole(userId);  // 권한 확인
        return mannerRepository.findAll().stream()
                .map(AdminConverter::toAdminMannerResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteManner(Long mannerId, String userId) {
        checkAdminRole(userId);  // 권한 확인
        mannerRepository.deleteById(mannerId);
    }

    @Override
    @Transactional
    public void saveManner(AdminMannerRequest request, String userId) {
        checkAdminRole(userId);  // 권한 확인
        Manner manner = AdminConverter.toManner(request);
        mannerRepository.save(manner);
    }

    @Override
    @Transactional
    public void updateManner(Long mannerId, AdminMannerRequest request, String userId) {
        checkAdminRole(userId);
        Manner existingManner = mannerRepository.findById(mannerId)
                .orElseThrow(() -> new MannerException(ErrorStatus.MANNER_NOT_FOUND));

        existingManner.updateManner(
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getImageUrl()
        );
    }

    @Override
    public List<AdminQuizResponse> getAllQuizzes(String userId) {
        checkAdminRole(userId);  // 권한 확인
        return quizRepository.findAll().stream()
                .map(AdminConverter::toAdminQuizResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveQuiz(AdminQuizRequest request, String userId) {
        checkAdminRole(userId);  // 권한 확인
        Quiz quiz = AdminConverter.toQuiz(request);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public void updateQuiz(Long quizId, AdminQuizRequest request, String userId) {
        checkAdminRole(userId);
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        existingQuiz.updateQuiz(
                request.getCategory(),
                request.getQuestion(),
                request.getAnswer(),
                request.getDescription(),
                request.getQuestionDetail(),
                request.getQuizLevel()
            );
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId, String userId) {
        checkAdminRole(userId);  // 권한 확인
        quizRepository.deleteById(quizId);
    }

    @Override
    public List<AdminVocaResponse> getAllVocas(String userId) {
        checkAdminRole(userId);  // 권한 확인
        return vocaRepository.findAll().stream()
                .map(AdminConverter::toAdminVocaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveVoca(AdminVocaRequest request, String userId) {
        checkAdminRole(userId);  // 권한 확인
        vocaRepository.save(AdminConverter.toVoca(request));
    }

    @Override
    @Transactional
    public void updateVoca(Long vocaId, AdminVocaRequest request, String userId) {
        checkAdminRole(userId);
        Voca existingVoca = vocaRepository.findById(vocaId)
                .orElseThrow(() -> new VocaException(ErrorStatus.VOCA_NOT_FOUND));

        existingVoca.updateVoca(
                request.getCategory(),
                request.getTerm(),
                request.getDescription(),
                request.getExample()
        );
    }

    @Override
    @Transactional
    public void deleteVoca(Long vocaId, String userId) {
        checkAdminRole(userId);  // 권한 확인
        vocaRepository.deleteById(vocaId);
    }

    @Override
    public List<AdminFeedbackResponse> getAllFeedbacks(String userId) {
        checkAdminRole(userId);
        return feedbackRepository.findAll().stream()
                .map(AdminConverter::toAdminFeedbackResponse)
                .collect(Collectors.toList());
    }
}