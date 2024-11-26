package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class QuizException extends GeneralException {

    public QuizException(ErrorStatus code) {
        super(code);
    }
}
