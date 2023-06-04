package com.jjbacsa.jjbacsabackend.etc.annotations;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class ListSizeValidator implements ConstraintValidator<IsValidListSize, List<?>> {

    private int maxSize;
    private int minSize;
    private String message;

    @Override
    public void initialize(IsValidListSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.max();
        this.minSize = constraintAnnotation.min();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.size() <= maxSize && value.size() >= minSize) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
    }
}
