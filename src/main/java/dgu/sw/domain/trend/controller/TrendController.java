package dgu.sw.domain.trend.controller;

import dgu.sw.domain.trend.dto.TrendDTO;
import dgu.sw.domain.trend.service.TrendService;
import dgu.sw.global.ApiResponse;
import dgu.sw.global.status.SuccessStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trends")
public class TrendController {

    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping
    public ApiResponse<List<TrendDTO>> getTrends() {
        List<TrendDTO> trends = trendService.fetchTrends();
        if (trends.isEmpty()) {
            return ApiResponse.onFailure("TREND404", "트렌드 데이터를 가져올 수 없습니다.", null);
        }
        return ApiResponse.of(SuccessStatus._OK, trends);
    }
}
