package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminFeedbackResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminLoginResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminLoginRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminQuizResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminVocaResponse;

import java.util.List;

public interface AdminService {
    AdminLoginResponse login(AdminLoginRequest request);
    List<AdminUserResponse> getAllUsers(Long userId);
    List<AdminMannerResponse> getAllManners(Long userId);
    void deleteManner(Long mannerId, Long userId);
    void saveManner(AdminMannerRequest request, Long userId);
    void updateManner(Long mannerId, AdminMannerRequest request, Long userId);
    List<AdminQuizResponse> getAllQuizzes(Long userId);
    void saveQuiz(AdminQuizRequest request, Long userId);
    void updateQuiz(Long quizId, AdminQuizRequest request, Long userId);
    void deleteQuiz(Long quizId, Long userId);
    List<AdminVocaResponse> getAllVocas(Long userId);
    void saveVoca(AdminVocaRequest request, Long userId);
    void updateVoca(Long vocaId, AdminVocaRequest request, Long userId);
    void deleteVoca(Long vocaId, Long userId);
    List<AdminFeedbackResponse> getAllFeedbacks(Long userId);
}
