package com.onboard.frontend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.onboard.frontend.constraints.UsernameExists;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.web.SessionService;

public class UsernameExistsValidator implements ConstraintValidator<UsernameExists, String> {

    public static final Logger logger = LoggerFactory.getLogger(EmailExistsValidator.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService sessionService;

    private boolean exist;

    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        String currentUserUsername = sessionService.getCurrentUser().getUsername();
        if (currentUserUsername != null && arg0.equals(currentUserUsername)) {
            return true;
        }
        return !(this.exist ^ accountService.getUserByEmailOrUsername(arg0) != null);
    }

    public void initialize(UsernameExists constraintAnnotation) {
        this.exist = constraintAnnotation.exist();
    }
}
