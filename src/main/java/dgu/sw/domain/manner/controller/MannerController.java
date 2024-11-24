package dgu.sw.domain.manner.controller;

import dgu.sw.domain.manner.dto.MannerDetailDTO;
import dgu.sw.domain.manner.dto.MannerListDTO;
import dgu.sw.domain.manner.service.MannerService;
import dgu.sw.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manners") // 공통 경로
public class MannerController {
    private final MannerService mannerService;

    // 카테고리 목록 반환
    @GetMapping
    public ApiResponse<List<String>> getCategories() {
        List<String> categories = mannerService.getCategories();
        return ApiResponse.onSuccess(categories);
    }

    // 특정 카테고리의 매너 리스트 반환
    @GetMapping(params = "category")
    public ApiResponse<List<MannerListDTO>> getMannersByCategory(@RequestParam("category") String category) {
        List<MannerListDTO> manners = mannerService.getMannersByCategory(category);
        return ApiResponse.onSuccess(manners);
    }

    // 특정 매너 설명서의 디테일 반환
    @GetMapping("/{mannerId}")
    public ApiResponse<MannerDetailDTO> getMannerDetail(@PathVariable("mannerId") Long mannerId) {
        MannerDetailDTO mannerDetail = mannerService.getMannerDetail(mannerId);
        return ApiResponse.onSuccess(mannerDetail);
    }
}

