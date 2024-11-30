package dgu.sw.domain.voca.controller;

import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaListResponse;
import dgu.sw.domain.voca.dto.VocaDTO.VocaResponse.VocaFavoriteResponse;
import dgu.sw.domain.voca.service.VocaService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voca")
@Tag(name = "Voca 컨트롤러", description = "업무 용어 관련 API")
public class VocaController {

    private final VocaService vocaService;

    @GetMapping
    @Operation(summary = "업무 용어 리스트 조회", description = "카테고리별 업무 용어 리스트를 반환합니다.")
    public ApiResponse<List<VocaListResponse>> getVocaList(
            @RequestParam String category,
            Authentication authentication) {
        List<VocaListResponse> response = vocaService.getVocaList(authentication.getName(), category);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/search")
    @Operation(summary = "업무 용어 검색", description = "키워드로 업무 용어를 검색합니다.")
    public ApiResponse<List<VocaListResponse>> searchVoca(@RequestParam String keyword) {
        List<VocaListResponse> response = vocaService.searchVoca(keyword);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/likes/{vocaId}")
    @Operation(summary = "업무 용어 즐겨찾기 추가", description = "업무 용어를 즐겨찾기에 추가합니다.")
    public ApiResponse<String> addFavorite(@PathVariable Long vocaId, Authentication authentication) {
        vocaService.addFavorite(authentication.getName(), vocaId);
        return ApiResponse.onSuccess("즐겨찾기에 추가되었습니다.");
    }

    @DeleteMapping("/likes/{vocaId}")
    @Operation(summary = "업무 용어 즐겨찾기 삭제", description = "업무 용어를 즐겨찾기에서 삭제합니다.")
    public ApiResponse<String> removeFavorite(@PathVariable Long vocaId, Authentication authentication) {
        vocaService.removeFavorite(authentication.getName(), vocaId);
        return ApiResponse.onSuccess("즐겨찾기에서 삭제되었습니다.");
    }

    @GetMapping("/likes")
    @Operation(summary = "즐겨찾기 조회", description = "사용자의 즐겨찾기 리스트를 반환합니다.")
    public ApiResponse<List<VocaFavoriteResponse>> getFavorites(Authentication authentication) {
        List<VocaFavoriteResponse> response = vocaService.getFavorites(authentication.getName());
        return ApiResponse.onSuccess(response);
    }
}
