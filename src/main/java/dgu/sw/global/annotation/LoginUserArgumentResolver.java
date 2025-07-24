package dgu.sw.global.annotation;

import dgu.sw.global.security.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @LoginUser 어노테이션이 붙은 컨트롤러 메서드 파라미터에 대해
 * 현재 인증된 사용자의 userId(Long)를 주입해주는 리졸버
 */
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 해당 리졸버가 어떤 파라미터에 적용되는지 판단
     *
     * 조건:
     * 1. @LoginUser 어노테이션이 붙어있고
     * 2. 파라미터 타입이 String인 경우
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().equals(String.class);
    }

    /**
     * 실제 파라미터에 주입할 값을 반환하는 로직
     * - SecurityContext에서 Authentication 객체를 가져와
     * - principal이 CustomUserDetails 타입이라면
     * - userId를 반환
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {

        // 현재 SecurityContext에서 인증 객체 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal이 CustomUserDetails이면 userId 반환
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return String.valueOf(userDetails.getId());
        }

        // 인증 정보가 없거나 타입이 다를 경우 null 반환 (또는 예외로 처리 가능)
        return null;
    }
}
