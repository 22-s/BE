package dgu.sw.global.exception;

import dgu.sw.global.status.ErrorStatus;

public class AdminException extends GeneralException {
  public AdminException(ErrorStatus code) {
    super(code);
  }
}
