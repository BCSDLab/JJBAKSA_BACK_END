package com.jjbacsa.jjbacsabackend.etc.annotations;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class ListSizeValidator implements ConstraintValidator<IsValidListSize, List<?>> {

    private int maxSize;
    private int minSize;

    @Override
    public void initialize(IsValidListSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.max();
        this.minSize = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.size() <= maxSize && value.size() >= minSize) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("리스트 원소의 갯수는 최소 %s, 최대 %s 개 입니다.", minSize, maxSize))
                    .addConstraintViolation();
            return false;
        }
    }
}
