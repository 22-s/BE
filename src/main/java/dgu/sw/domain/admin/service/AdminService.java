package dgu.sw.domain.admin.service;

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
    List<AdminUserResponse> getAllUsers(String userId);
    List<AdminMannerResponse> getAllManners(String userId);
    void deleteManner(Long mannerId, String userId);
    void saveManner(AdminMannerRequest request, String userId);
    void updateManner(Long mannerId, AdminMannerRequest request, String userId);
    List<AdminQuizResponse> getAllQuizzes(String userId);
    void saveQuiz(AdminQuizRequest request, String userId);
    void updateQuiz(Long quizId, AdminQuizRequest request, String userId);
    void deleteQuiz(Long quizId, String userId);
    List<AdminVocaResponse> getAllVocas(String userId);
    void saveVoca(AdminVocaRequest request, String userId);
    void updateVoca(Long vocaId, AdminVocaRequest request, String userId);
    void deleteVoca(Long vocaId, String userId);
}
