package dgu.sw.domain.admin.controller;

import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminLoginRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.*;
import dgu.sw.domain.admin.service.AdminService;
import dgu.sw.global.ApiResponse;
import dgu.sw.global.annotation.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin 컨트롤러", description = "관리자 관련 화면 렌더링")
public class AdminController {

    private final AdminService adminService;

    // 관리자 로그인
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        return ApiResponse.onSuccess(adminService.login(request));
    }

    // 사용자 전체 조회
    @GetMapping("/users")
    public ApiResponse<List<AdminUserResponse>> getAllUsers(@LoginUser Long userId) {
        return ApiResponse.onSuccess(adminService.getAllUsers(userId));
    }

    // 매너 전체 조회
    @GetMapping("/manners")
    public ApiResponse<List<AdminMannerResponse>> getAllManners(@LoginUser Long userId) {
        return ApiResponse.onSuccess(adminService.getAllManners(userId));
    }

    // 매너 등록
    @PostMapping("/manners")
    public ApiResponse<String> saveManner(@RequestBody AdminMannerRequest request, @LoginUser Long userId) {
        adminService.saveManner(request, userId);
        return ApiResponse.onSuccess("매너가 등록되었습니다.");
    }

    // 매너 수정
    @PatchMapping("/manners/{mannerId}")
    public ApiResponse<String> updateManner(@PathVariable Long mannerId, @RequestBody AdminMannerRequest request, @LoginUser Long userId) {
        adminService.updateManner(mannerId, request, userId);
        return ApiResponse.onSuccess("매너가 수정되었습니다.");
    }

    // 매너 삭제
    @DeleteMapping("/manners/{mannerId}")
    public ApiResponse<String> deleteManner(@PathVariable Long mannerId, @LoginUser Long userId) {
        adminService.deleteManner(mannerId, userId);
        return ApiResponse.onSuccess("매너가 삭제되었습니다.");
    }

    // 퀴즈 전체 조회
    @GetMapping("/quizzes")
    public ApiResponse<List<AdminQuizResponse>> getAllQuizzes(@LoginUser Long userId) {
        return ApiResponse.onSuccess(adminService.getAllQuizzes(userId));
    }

    // 퀴즈 등록
    @PostMapping("/quizzes")
    public ApiResponse<String> saveQuiz(@RequestBody AdminQuizRequest request, @LoginUser Long userId) {
        adminService.saveQuiz(request, userId);
        return ApiResponse.onSuccess("퀴즈가 등록되었습니다.");
    }

    // 퀴즈 수정
    @PatchMapping("/quizzes/{quizId}")
    public ApiResponse<String> updateQuiz(@PathVariable Long quizId, @RequestBody AdminQuizRequest request, @LoginUser Long userId) {
        adminService.updateQuiz(quizId, request, userId);
        return ApiResponse.onSuccess("퀴즈가 수정되었습니다.");
    }

    // 퀴즈 삭제
    @DeleteMapping("/quizzes/{quizId}")
    public ApiResponse<String> deleteQuiz(@PathVariable Long quizId, @LoginUser Long userId) {
        adminService.deleteQuiz(quizId, userId);
        return ApiResponse.onSuccess("퀴즈가 삭제되었습니다.");
    }

    // 단어 전체 조회
    @GetMapping("/vocas")
    public ApiResponse<List<AdminVocaResponse>> getAllVocas(@LoginUser Long userId) {
        return ApiResponse.onSuccess(adminService.getAllVocas(userId));
    }

    // 단어 등록
    @PostMapping("/vocas")
    public ApiResponse<String> saveVoca(@RequestBody AdminVocaRequest request, @LoginUser Long userId) {
        adminService.saveVoca(request, userId);
        return ApiResponse.onSuccess("단어가 등록되었습니다.");
    }

    // 단어 수정
    @PatchMapping("/vocas/{vocaId}")
    public ApiResponse<String> updateVoca(@PathVariable Long vocaId, @RequestBody AdminVocaRequest request, @LoginUser Long userId) {
        adminService.updateVoca(vocaId, request, userId);
        return ApiResponse.onSuccess("단어가 수정되었습니다.");
    }

    // 단어 삭제
    @DeleteMapping("/vocas/{vocaId}")
    public ApiResponse<String> deleteVoca(@PathVariable Long vocaId, @LoginUser Long userId) {
        adminService.deleteVoca(vocaId, userId);
        return ApiResponse.onSuccess("단어가 삭제되었습니다.");
    }

    // 앱 피드백 조회
    @GetMapping("/app")
    public ApiResponse<List<AdminFeedbackResponse>> getFeedback(@LoginUser Long userId) {
        return ApiResponse.onSuccess(adminService.getAllFeedbacks(userId));
    }
}