package com.onboard.frontend.controller.page.account;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.controller.page.account.form.ResetPasswordForm;
import com.onboard.frontend.service.account.AccountService;

@Controller
@RequestMapping("/account/{uid}/resetpassword/{token}")
public class ResetPasswordController {

    public static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);
    private static final String RESET_PASSWORD_TOKEN_EXPIRES = "error/ResetPasswordTokenExpires";
    private static final String RESET_PASSWORD_TPL = "account/ResetPassword";
    private static final String RESET_PASSWORD_RESULT_TPL = "account/ResetResult";

    @Autowired
    private AccountService accountService;

    @ModelAttribute("resetCommand")
    public ResetPasswordForm getResetPasswordForm() {
        return new ResetPasswordForm();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showResetPasswordPage(@PathVariable("uid") int uid, @PathVariable("token") String token,
            @ModelAttribute("resetCommand") ResetPasswordForm form) {
        if (!accountService.authenticateForgetToken(uid, token)) {
            return RESET_PASSWORD_TOKEN_EXPIRES;
        }
        form.setToken(token);

        return RESET_PASSWORD_TPL;
    }

    /**
     * 重置密码，GET请求经过验证token后进入重置页面。POST请求更新密码，并跳转回登录页面
     * 
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String resetPassword(@PathVariable("uid") int uid, @PathVariable("token") String token,
            @ModelAttribute("resetCommand") @Valid ResetPasswordForm form, BindingResult result) {

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                logger.info("error in register form validation: {}", error);
            }

            return RESET_PASSWORD_TPL;
        }
        if (!accountService.resetPassword(uid, token, form)) {
            return RESET_PASSWORD_TPL;
        }

        return RESET_PASSWORD_RESULT_TPL;
    }
}
