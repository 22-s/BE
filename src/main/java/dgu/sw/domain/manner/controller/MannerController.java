package dgu.sw.domain.manner.controller;

import dgu.sw.domain.manner.dto.MannerDTO;
import dgu.sw.domain.manner.service.MannerService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerFavoriteResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerDetailResponse;
import dgu.sw.domain.manner.dto.MannerDTO.MannerResponse.MannerListResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manners")
@Tag(name = "매너 설명서 컨트롤러", description = "매너 설명서 관련 API")
public class MannerController {
    private final MannerService mannerService;

    // 1. 특정 카테고리의 매너 리스트 반환
    @GetMapping(params = "category")
    @Operation(summary = "카테고리 리스트 반환", description = "특정 카테고리의 매너 리스트를 반환합니다.")
    public ApiResponse<List<MannerListResponse>> getMannersByCategory(
            @RequestParam int category,
            Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : null;
        return ApiResponse.onSuccess(mannerService.getMannersByCategory(category, userId));
    }

    // 2. 특정 매너 설명서 상세 조회
    @GetMapping("/{mannerId}")
    @Operation(summary = "매너 디테일 반환", description = "특정 매너 설명서의 디테일을 반환합니다.")
    public ApiResponse<MannerDetailResponse> getMannerDetail(
            @PathVariable Long mannerId,
            Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : null;
        return ApiResponse.onSuccess(mannerService.getMannerDetail(mannerId, userId));
    }

    // 3. 전체 검색
    @GetMapping("/search")
    @Operation(summary = "전체 검색", description = "전체 매너 설명서를 기반으로 검색합니다.")
    public ApiResponse<List<MannerListResponse>> searchManners(@RequestParam String keyword) {
        return ApiResponse.onSuccess(mannerService.searchManners(keyword));
    }

    // 4. 선택된 카테고리 내 검색
    @GetMapping(value = "/search/category")
    @Operation(summary = "카테고리 내 검색", description = "특정 카테고리 내에서 매너 설명서를 검색합니다.")
    public ApiResponse<List<MannerListResponse>> searchMannersByCategory(
            @RequestParam int category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.onSuccess(mannerService.searchMannersByCategory(category, keyword));
    }

    // 5. 즐겨찾기 추가
    @PostMapping("/likes/{mannerId}")
    @Operation(summary = "즐겨찾기 추가", description = "즐겨찾기 리스트에 추가합니다.")
    public ApiResponse<String> addFavorite(@PathVariable Long mannerId, Authentication authentication) {
        mannerService.addFavorite(authentication.getName(), mannerId);
        return ApiResponse.onSuccess("즐겨찾기에 추가되었습니다.");
    }

    // 6. 즐겨찾기 삭제
    @DeleteMapping("/likes/{mannerId}")
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 리스트에서 삭제합니다.")
    public ApiResponse<String> removeFavorite(@PathVariable Long mannerId, Authentication authentication) {
        mannerService.removeFavorite(authentication.getName(), mannerId);
        return ApiResponse.onSuccess("즐겨찾기에서 삭제되었습니다.");
    }

    // 7. 즐겨찾기 조회
    @GetMapping("/likes")
    @Operation(summary = "즐겨찾기 조회", description = "로그인한 사용자의 즐겨찾기 리스트를 조회합니다.")
    public ApiResponse<List<MannerFavoriteResponse>> getFavorites(Authentication authentication) {
        return ApiResponse.onSuccess(mannerService.getFavorites(authentication.getName()));
    }
}