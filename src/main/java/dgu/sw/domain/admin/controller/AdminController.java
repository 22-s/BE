package dgu.sw.domain.admin.controller;

import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin 컨트롤러", description = "관리자 관련 화면 렌더링")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("")
    public String adminHome() {
        return "admin/home"; // templates/admin/home.html
    }

    @GetMapping("/users")
    public String showUserList(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users"; // resources/templates/admin/users.html
    }

    @GetMapping("/manners")
    public String showManners(Model model) {
        model.addAttribute("manners", adminService.getAllManners());
        return "admin/manners_list";
    }

    @PostMapping("/manners/{mannerId}/delete")
    public String deleteManner(@PathVariable Long mannerId) {
        adminService.deleteManner(mannerId);
        return "redirect:/admin/manners";
    }

    @GetMapping("/manners/new")
    public String showMannerForm(Model model) {
        model.addAttribute("manner", new AdminMannerRequest());
        return "admin/manners_form"; // 등록 폼
    }

    @PostMapping("/manners")
    public String saveManner(AdminMannerRequest request) {
        adminService.saveManner(request);
        return "redirect:/admin/manners";
    }

    @GetMapping("/quizzes")
    public String showQuizzes(Model model) {
        model.addAttribute("quizzes", adminService.getAllQuizzes());
        return "admin/quizzes_list";
    }

    @GetMapping("/quizzes/new")
    public String showQuizForm(Model model) {
        model.addAttribute("quiz", new AdminQuizRequest());
        return "admin/quizzes_form";
    }

    @PostMapping("/quizzes")
    public String saveQuiz(AdminQuizRequest request) {
        adminService.saveQuiz(request);
        return "redirect:/admin/quizzes";
    }

    @PostMapping("/quizzes/{quizId}/delete")
    public String deleteQuiz(@PathVariable Long quizId) {
        adminService.deleteQuiz(quizId);
        return "redirect:/admin/quizzes";
    }


    @GetMapping("/vocas")
    public String showVocaList(Model model) {
        model.addAttribute("vocas", adminService.getAllVocas());
        return "admin/vocas_list";
    }

    @GetMapping("/vocas/new")
    public String showVocaForm(Model model) {
        model.addAttribute("voca", new AdminVocaRequest());
        return "admin/vocas_form";
    }

    @PostMapping("/vocas")
    public String saveVoca(AdminVocaRequest request) {
        adminService.saveVoca(request);
        return "redirect:/admin/vocas";
    }

    @PostMapping("/vocas/{vocaId}/delete")
    public String deleteVoca(@PathVariable Long vocaId) {
        adminService.deleteVoca(vocaId);
        return "redirect:/admin/vocas";
    }
}
