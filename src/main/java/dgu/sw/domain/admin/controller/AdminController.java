package dgu.sw.domain.admin.controller;

import dgu.sw.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
