package com.jjbacsa.jjbacsabackend.etc.exception;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;

public class ApiException extends CriticalException{
    public ApiException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);
    }

    public ApiException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
