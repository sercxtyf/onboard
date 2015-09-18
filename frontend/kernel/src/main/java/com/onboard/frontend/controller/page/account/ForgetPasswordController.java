package com.onboard.frontend.controller.page.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.controller.page.account.form.ForgetPasswordForm;
import com.onboard.frontend.service.account.AccountService;

@Controller
@RequestMapping("/account")
public class ForgetPasswordController {

    public static final Logger logger = LoggerFactory.getLogger(ForgetPasswordController.class);

    private static final String FORGET_COMMAND = "forgetCommand";
    private static final String FORGETPASSWORD_VIEW = "account/ForgetPassword";

    @Autowired
    private AccountService accountService;

    @ModelAttribute(FORGET_COMMAND)
    public ForgetPasswordForm getForgetPasswordForm() {
        return new ForgetPasswordForm();
    }

    @RequestMapping(value = "/forgetpassword", method = RequestMethod.GET)
    public String showForgetPasswordPage(@ModelAttribute(FORGET_COMMAND) ForgetPasswordForm form) {

        return FORGETPASSWORD_VIEW;
    }
}
