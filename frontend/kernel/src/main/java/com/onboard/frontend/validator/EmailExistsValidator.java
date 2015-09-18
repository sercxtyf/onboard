package com.onboard.frontend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.onboard.frontend.constraints.EmailExists;
import com.onboard.frontend.service.account.AccountService;

public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {

    public static final Logger logger = LoggerFactory.getLogger(EmailExistsValidator.class);

    @Autowired
    private AccountService accountService;

    private boolean exist;

    public void initialize(EmailExists arg0) {
        this.exist = arg0.exist();
    }

    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        return !(this.exist ^ accountService.getUserByEmailOrUsername(arg0) != null);
    }

}
