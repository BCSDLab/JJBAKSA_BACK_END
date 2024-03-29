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
    INVALID_TOKEN_TYPE(15, "잘못된 토큰 타입입니다.", HttpStatus.BAD_REQUEST),
    ZERO_RESULTS_EXCEPTION(16, "구글 API 검색 결과가 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_EXCEPTION(17, "정보를 가져올 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_REQUEST_EXCEPTION(18, "API 요쳥 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    OVER_QUERY_LIMIT_EXCEPTION(19, "API 요청을 수행할 수 없습니다.", HttpStatus.FORBIDDEN),
    REQUEST_DENIEDE_EXCEPTION(20, "API 요청이 거절됩니다.", HttpStatus.UNAUTHORIZED),
    JSON_PROCESSING_EXCEPTION(21, "JSON 변환 과정에서 에러가 발생합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REQUIRED_ATTRIBUTE_MISSING_EXCEPTION(22, "API 필수 속성이 없습니다.", HttpStatus.NOT_FOUND),
    SHOP_NOT_EXISTS_EXCEPTION(23, "상점이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_EXISTS_EXCEPTION(24, "리뷰가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_PERMISSION_REVIEW(25, "리뷰 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXISTS_EXCEPTION(26, "이미지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_IMAGE(27, "올바르지 않은 이미지 파일입니다.", HttpStatus.BAD_REQUEST),
    SCRAP_NOT_EXISTS_EXCEPTION(28, "스크랩이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    SCRAP_DUPLICATE_EXCEPTION(29, "같은 상점에 대한 스크랩이 존재합니다.", HttpStatus.BAD_REQUEST),
    SCRAP_DIRECTORY_NOT_EXISTS_EXCEPTION(30, "스크랩 디렉토리가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    SCRAP_DIRECTORY_DUPLICATE_EXCEPTION(31, "같은 이름의 디렉토리가 존재합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAIL_EXCEPTION(32, "이미지 업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_SIZE_OVERFLOW_EXCEPTION(33, "이미지 파일크기가 10MB이상 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SOCIAL_TYPE(34, "유효하지 않은 소셜 로그인 타입입니다.", HttpStatus.BAD_REQUEST),
    BAD_AUTHENTICATION_CODE(35, "잘못된 인가 코드입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_EXPIRED_EXCEPTION(36, "이메일 인증 시간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_CODE_FAIL_EXCEPTION(37, "인증 번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_EXCEED_EXCEPTION(38, "이메일 전송 횟수가 초과되었습니다.", HttpStatus.BAD_REQUEST),
    SOCIAL_ACCOUNT_EXCEPTION(39, "소셜 로그인 사용자는 수행할 수 없습니다.", HttpStatus.BAD_REQUEST),
    IMAGE_RESIZING_EXCEPTION(40, "이미지 리사이징에 실패했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AUTHORITY(41, "사용자 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    POST_NOT_EXISTS_EXCEPTION(42, "게시글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AUTHENTICATE_EMAIL(43, "이메일 인증이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS_EMAIL(44, "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_EMAIL_EXCEPTION(45, "이메일이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL_EXCEPTION(46, "회원이 아니시거나, 아이디 또는 비밀번호를 잘못 입력했습니다.", HttpStatus.CONFLICT),
    INQUIRY_NOT_EXISTS_EXCEPTION(47, "문의글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PERMISSION_INQUIRY(48, "문의글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
    CONTINUAL_API_EXCEPTION(49, "연속적으로 외부 API 요청에 실패합니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SHOP_EXCEPTION(50, "상점 정보 조회에 실패합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_INCORRECT_EXCEPTION(51, "현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    WEEK_DAY_EXCEPTION(52, "요일이 유효하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
