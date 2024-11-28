package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class MannerException extends GeneralException {

    public MannerException(ErrorStatus code) {
        super(code);
    }
}