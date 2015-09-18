package com.onboard.frontend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;
import com.onboard.frontend.constraints.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    public void initialize(Password constraintAnnotation) {

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Strings.isNullOrEmpty(value) || value.length() >= 6 && value.length() <= 20;
    }

}
