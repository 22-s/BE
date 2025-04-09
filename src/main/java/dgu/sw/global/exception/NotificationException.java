package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class NotificationException extends GeneralException {
    public NotificationException(ErrorStatus code) {
        super(code);
    }
}