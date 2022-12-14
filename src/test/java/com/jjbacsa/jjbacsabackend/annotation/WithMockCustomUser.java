package com.jjbacsa.jjbacsabackend.annotation;

import com.jjbacsa.jjbacsabackend.annotation.CustomSecurityContext.WithMockCustomUserSecurityContextFactory;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String id() default "5";

    UserType role() default UserType.ROOT;
}
