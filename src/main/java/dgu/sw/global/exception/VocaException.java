package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class VocaException extends GeneralException {
    public VocaException(ErrorStatus code) {
        super(code);
    }
}
