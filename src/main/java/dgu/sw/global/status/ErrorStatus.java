package dgu.sw.global.status;

import dgu.sw.global.BaseErrorCode;
import dgu.sw.global.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 퀴즈 관련 에러
    QUIZ_NOT_FOUND(HttpStatus.BAD_REQUEST, "QUIZ4001", "존재하지 않는 퀴즈입니다."),
    USER_QUIZ_ALREADY_SOLVED(HttpStatus.BAD_REQUEST, "QUIZ4002", "이미 푼 퀴즈입니다."),
    REVIEW_ALREADY_ADDED(HttpStatus.BAD_REQUEST, "QUIZ4003", "이미 복습 리스트에 추가된 퀴즈입니다."),
    QUIZ_LOCKED(HttpStatus.FORBIDDEN, "QUIZ4004", "잠겨있는 퀴즈입니다."),
    INVALID_QUIZ_ANSWER(HttpStatus.BAD_REQUEST, "QUIZ4005", "잘못된 퀴즈 답안입니다."),
    QUIZ_SEARCH_NO_RESULTS(HttpStatus.BAD_REQUEST, "QUIZ4006", "검색 결과가 없습니다."),

    // 사용자 관련 에러
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4001", "이미 존재하는 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER4002", "이메일 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER4003", "토큰이 존재하지 않습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "USER4004", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "USER4005", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER4006", "Refresh Token이 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4007", "존재하지 않는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
