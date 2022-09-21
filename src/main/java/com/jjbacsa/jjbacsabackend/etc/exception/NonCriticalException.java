package com.jjbacsa.jjbacsabackend.etc.exception;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;


public class NonCriticalException extends BaseException {

	public NonCriticalException(String className, ErrorMessage errorMessage) {
		super(className, errorMessage);

	}
	public NonCriticalException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
