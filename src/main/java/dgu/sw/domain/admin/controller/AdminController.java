package dgu.sw.domain.admin.controller;

import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminLoginResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminLoginRequest;;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminQuizResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminVocaResponse;
import dgu.sw.domain.admin.service.AdminService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<List<AdminUserResponse>> getAllUsers(Authentication authentication) {
        return ApiResponse.onSuccess(adminService.getAllUsers(authentication.getName()));
    }

    // 매너 전체 조회
    @GetMapping("/manners")
    public ApiResponse<List<AdminMannerResponse>> getAllManners(Authentication authentication) {
        return ApiResponse.onSuccess(adminService.getAllManners(authentication.getName()));
    }

    // 매너 등록
    @PostMapping("/manners")
    public ApiResponse<String> saveManner(@RequestBody AdminMannerRequest request, Authentication authentication) {
        adminService.saveManner(request, authentication.getName());
        return ApiResponse.onSuccess("매너가 등록되었습니다.");
    }

    // 매너 수정
    @PatchMapping("/manners/{mannerId}")
    public ApiResponse<String> updateManner(@PathVariable Long mannerId, @RequestBody AdminMannerRequest request, Authentication authentication) {
        adminService.updateManner(mannerId, request, authentication.getName());
        return ApiResponse.onSuccess("매너가 수정되었습니다.");
    }

    // 매너 삭제
    @DeleteMapping("/manners/{mannerId}")
    public ApiResponse<String> deleteManner(@PathVariable Long mannerId, Authentication authentication) {
        adminService.deleteManner(mannerId, authentication.getName());
        return ApiResponse.onSuccess("매너가 삭제되었습니다.");
    }

    // 퀴즈 전체 조회
    @GetMapping("/quizzes")
    public ApiResponse<List<AdminQuizResponse>> getAllQuizzes(Authentication authentication) {
        return ApiResponse.onSuccess(adminService.getAllQuizzes(authentication.getName()));
    }

    // 퀴즈 등록
    @PostMapping("/quizzes")
    public ApiResponse<String> saveQuiz(@RequestBody AdminQuizRequest request, Authentication authentication) {
        adminService.saveQuiz(request, authentication.getName());
        return ApiResponse.onSuccess("퀴즈가 등록되었습니다.");
    }

    // 퀴즈 수정
    @PatchMapping("/quizzes/{quizId}")
    public ApiResponse<String> updateQuiz(@PathVariable Long quizId, @RequestBody AdminQuizRequest request, Authentication authentication) {
        adminService.updateQuiz(quizId, request, authentication.getName());
        return ApiResponse.onSuccess("퀴즈가 수정되었습니다.");
    }

    // 퀴즈 삭제
    @DeleteMapping("/quizzes/{quizId}")
    public ApiResponse<String> deleteQuiz(@PathVariable Long quizId, Authentication authentication) {
        adminService.deleteQuiz(quizId, authentication.getName());
        return ApiResponse.onSuccess("퀴즈가 삭제되었습니다.");
    }

    // 단어 전체 조회
    @GetMapping("/vocas")
    public ApiResponse<List<AdminVocaResponse>> getAllVocas(Authentication authentication) {
        return ApiResponse.onSuccess(adminService.getAllVocas(authentication.getName()));
    }

    // 단어 등록
    @PostMapping("/vocas")
    public ApiResponse<String> saveVoca(@RequestBody AdminVocaRequest request, Authentication authentication) {
        adminService.saveVoca(request, authentication.getName());
        return ApiResponse.onSuccess("단어가 등록되었습니다.");
    }

    // 단어 수정
    @PatchMapping("/vocas/{vocaId}")
    public ApiResponse<String> updateVoca(@PathVariable Long vocaId, @RequestBody AdminVocaRequest request, Authentication authentication) {
        adminService.updateVoca(vocaId, request, authentication.getName());
        return ApiResponse.onSuccess("단어가 수정되었습니다.");
    }

    // 단어 삭제
    @DeleteMapping("/vocas/{vocaId}")
    public ApiResponse<String> deleteVoca(@PathVariable Long vocaId, Authentication authentication) {
        adminService.deleteVoca(vocaId, authentication.getName());
        return ApiResponse.onSuccess("단어가 삭제되었습니다.");
    }
}