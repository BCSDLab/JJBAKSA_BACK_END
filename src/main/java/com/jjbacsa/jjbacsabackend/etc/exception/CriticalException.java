package com.jjbacsa.jjbacsabackend.etc.exception;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;


public class CriticalException extends BaseException {

	public CriticalException(String className, ErrorMessage errorMessage) {
		super(className, errorMessage);
	}
	public CriticalException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
