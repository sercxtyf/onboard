package com.onboard.frontend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.onboard.frontend.constraints.Username;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    public void initialize(Username constraintAnnotation) {

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return value.matches("^[a-zA-Z0-9_]{3,16}$");
    }

}
