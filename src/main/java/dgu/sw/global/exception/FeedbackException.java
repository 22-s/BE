package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class FeedbackException extends GeneralException {
    public FeedbackException(ErrorStatus code) {
        super(code);
    }
}