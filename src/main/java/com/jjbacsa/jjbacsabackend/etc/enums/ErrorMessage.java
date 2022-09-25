package com.jjbacsa.jjbacsabackend.etc.enums;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {
    UNDEFINED_EXCEPTION(0, "정의되지 않은 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NULL_POINTER_EXCEPTION(1, "NULL 여부를 확인해주세요.", HttpStatus.BAD_REQUEST),
    VALIDATION_FAIL_EXCEPTION(2, "입력값의 조건이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS_EXCEPTION(3, "사용자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_REQUEST_NOT_EXISTS_EXCEPTION(4, "팔로우 요청이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_REQUEST_MYSELF_EXCEPTION(5, "자신에게 팔로우를 요청할 수 없습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_REQUEST_DUPLICATION_EXCEPTION(6, "이미 팔로우를 요청한 사용자입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOW_REQUESTED_EXCEPTION(7, "이미 팔로우를 요청받은 사용자입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWED_EXCEPTION(8, "이미 팔로우된 사용자입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWED_EXCEPTION(9, "팔로우가 아닙니다.", HttpStatus.BAD_REQUEST),

    ALREADY_EXISTS_ACCOUNT(11, "이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),
    INVALID_TOKEN(12, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS(13, "유효하지 않은 접근입니다.", HttpStatus.CONFLICT),
    EXPIRED_TOKEN(14, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(15,  "잘못된 토큰 타입입니다.", HttpStatus.BAD_REQUEST)
    ;

    Integer code;
    String errorMessage;
    HttpStatus httpStatus;

    ErrorMessage(int code, String errorMessage, HttpStatus httpStatus) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }


    public Integer getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
