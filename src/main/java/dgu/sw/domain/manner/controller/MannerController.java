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
@RequestMapping("/api/manners")
public class MannerController {
    private final MannerService mannerService;

    // 1. 모든 카테고리 반환 + 카테고리 중 검색
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.onSuccess(mannerService.getCategories());
        }
        return ApiResponse.onSuccess(mannerService.searchCategories(keyword));
    }

    // 2. 특정 카테고리의 매너 리스트 반환 + 해당 카테고리 내 검색
    @GetMapping
    public ApiResponse<List<MannerListDTO>> getMannersByCategoryAndKeyword(
            @RequestParam("category") String category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.onSuccess(mannerService.getMannersByCategory(category));
        }
        return ApiResponse.onSuccess(mannerService.searchMannersByCategoryAndKeyword(category, keyword));
    }

    // 3. 특정 매너 설명서의 디테일 반환
    @GetMapping("/{mannerId}")
    public ApiResponse<MannerDetailDTO> getMannerDetail(@PathVariable("mannerId") Long mannerId) {
        return ApiResponse.onSuccess(mannerService.getMannerDetail(mannerId));
    }
}
