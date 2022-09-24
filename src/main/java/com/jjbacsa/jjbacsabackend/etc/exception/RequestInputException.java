package com.jjbacsa.jjbacsabackend.etc.exception;


import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;

public class RequestInputException extends NonCriticalException {

    public RequestInputException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);

    }
    public RequestInputException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
