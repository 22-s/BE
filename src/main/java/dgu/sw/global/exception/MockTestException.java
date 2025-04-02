package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class MockTestException extends GeneralException {

    public MockTestException(ErrorStatus code) {
        super(code);
    }
}