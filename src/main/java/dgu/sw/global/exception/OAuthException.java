package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class OAuthException extends GeneralException{
    public OAuthException(ErrorStatus code) {
        super(code);
    }
}
