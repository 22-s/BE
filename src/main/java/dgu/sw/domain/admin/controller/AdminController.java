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
    public AdminLoginResponse login(@RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }
    // 사용자 전체 조회
    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers(Authentication authentication) {
        return adminService.getAllUsers(authentication.getName());
    }

    // 매너 전체 조회
    @GetMapping("/manners")
    public List<AdminMannerResponse> getAllManners(Authentication authentication) {
        return adminService.getAllManners(authentication.getName());
    }

    // 매너 등록
    @PostMapping("/manners")
    public void saveManner(@RequestBody AdminMannerRequest request, Authentication authentication) {
        adminService.saveManner(request, authentication.getName());
    }

    // 매너 삭제
    @DeleteMapping("/manners/{mannerId}")
    public void deleteManner(@PathVariable Long mannerId, Authentication authentication) {
        adminService.deleteManner(mannerId, authentication.getName());
    }

    // 퀴즈 전체 조회
    @GetMapping("/quizzes")
    public List<AdminQuizResponse> getAllQuizzes(Authentication authentication) {
        return adminService.getAllQuizzes(authentication.getName());
    }

    // 퀴즈 등록
    @PostMapping("/quizzes")
    public void saveQuiz(@RequestBody AdminQuizRequest request, Authentication authentication) {
        adminService.saveQuiz(request, authentication.getName());
    }

    // 퀴즈 삭제
    @DeleteMapping("/quizzes/{quizId}")
    public void deleteQuiz(@PathVariable Long quizId, Authentication authentication) {
        adminService.deleteQuiz(quizId, authentication.getName());
    }

    // 단어 전체 조회
    @GetMapping("/vocas")
    public List<AdminVocaResponse> getAllVocas(Authentication authentication) {
        return adminService.getAllVocas(authentication.getName());
    }

    // 단어 등록
    @PostMapping("/vocas")
    public void saveVoca(@RequestBody AdminVocaRequest request, Authentication authentication) {
        adminService.saveVoca(request, authentication.getName());
    }

    // 단어 삭제
    @DeleteMapping("/vocas/{vocaId}")
    public void deleteVoca(@PathVariable Long vocaId, Authentication authentication) {
        adminService.deleteVoca(vocaId, authentication.getName());
    }
}