package dgu.sw.global.annotation;

import java.lang.annotation.*;

/**
 * 컨트롤러 메서드에서 현재 로그인한 사용자의 userId를 파라미터로 주입받기 위한 어노테이션
 * ex) public ApiResponse<List<QuizReviewResponse>> getReviewList(@LoginUser Long userId)
 */
@Target(ElementType.PARAMETER) // 파라미터에만 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 유지
@Documented
public @interface LoginUser {
}
