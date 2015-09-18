package com.onboard.frontend.controller.page.account;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.controller.page.account.form.RegistrationForm;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.web.SessionService;

@Controller
@RequestMapping("/account/signup")
public class SignupController {

    public static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    private static final String NEW_COMMAND = "newCommand";
    private static final String SIGNUP_VIEW = "account/Signup";

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService sessionService;

    @ModelAttribute(NEW_COMMAND)
    public RegistrationForm getUserRegistrationForm() {
        return new RegistrationForm();
    }

    /**
     * 显示注册页面
     * 
     * @param form
     *            注册表单
     * @return 视图Signup
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String initRegistrationForm(@ModelAttribute(NEW_COMMAND) RegistrationForm form, Model model) {
        // model.addAttribute("githubSignupCallback", String.format(GITHUBOAUTH, client_id, applicationHostUrl, scope, state));

        return SIGNUP_VIEW;
    }

    /**
     * 注册用户
     * 
     * @param form
     *            用户提交的注册表单
     * @param result
     *            表单绑定结果，用于判定表单是否验证通过
     * 
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String signUp(@ModelAttribute(NEW_COMMAND) @Valid RegistrationForm form, BindingResult result) {
        logger.info("start");
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                logger.info("error in register form validation: {}", error);
            }

            return SIGNUP_VIEW;
        }

        accountService.signup(form);

        User user = accountService.getUserByEmailOrUsername(form.getEmail());
        sessionService.setCurrentUser(user);

        return "redirect:/";
    }
}
