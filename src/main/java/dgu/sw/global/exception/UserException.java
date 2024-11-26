package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class UserException extends GeneralException {
    public UserException(ErrorStatus code) {
        super(code);
    }
}
