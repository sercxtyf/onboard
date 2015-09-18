package com.onboard.frontend.controller.page.account;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.onboard.frontend.controller.page.account.form.ThirdPartRegistrationForm;
import com.onboard.frontend.interceptors.RememberMeInterceptor;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.web.SessionService;

@Controller
@RequestMapping("/account")
public class ThirdPartSignupController {
    public static final Logger logger = LoggerFactory.getLogger(SignupController.class);
    private static final String BIND_EMAIL_SIGNUP_VIEM = "account/SignupBindEmail";

    // github parameters
    @Value("${data.github.client_id}")
    private String client_id;

    @Value("${data.github.scope}")
    private String scope;

    @Value("${data.github.state}")
    private String githubState;

    @Value("${data.host}")
    private String applicationHostUrl;

    /**
     * Cookie过期时间
     */
    @Value("${account.rememberMeExpired}")
    private int expiredTime;

    /**
     * Cookie Domain
     */
    @Value("${site.domain}")
    private String domain;

    private static final String GITHUBOAUTH = "https://github.com/login/oauth/authorize?client_id=%s&amp;"
            + "redirect_uri=%s/account/github/callback&amp;scope=%s&amp;state=%s";

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService session;

    private static final String NEW_COMMAND = "newCommand";

    @ModelAttribute(NEW_COMMAND)
    public ThirdPartRegistrationForm getUserRegistrationForm() {
        return new ThirdPartRegistrationForm();
    }

    @RequestMapping(value = "/thirdpartSignup", method = RequestMethod.GET)
    public String redirectThirdpartSignup() {
        String githubSignupCallback = String.format(GITHUBOAUTH, client_id, applicationHostUrl, scope, githubState);

        return "redirect:" + githubSignupCallback;
    }

    @RequestMapping(value = "/github/callback", method = RequestMethod.GET)
    public String getGitHubResponse(HttpServletResponse response, HttpServletRequest request,
            @ModelAttribute(NEW_COMMAND) ThirdPartRegistrationForm form, @RequestParam("code") String code,
            @RequestParam("state") String state, Model model) {
        if (!githubState.equals(state)) {
            return "home/Index";
        }
        String nextUrl = request.getParameter("next");
        Map<String, String> signupMap = accountService.thirdPartSignupCallback(code);
        Boolean exist = Boolean.valueOf(signupMap.get("exist"));
        if (exist) {
            User user = accountService.getUserByEmailOrUsername(signupMap.get("email"));
            session.setCurrentUser(user);
            String token = accountService.getRememberMeToken(user.getId());
            setCookie(response, RememberMeInterceptor.COOKIE_UID, String.valueOf(user.getId()), this.domain, this.expiredTime);
            setCookie(response, RememberMeInterceptor.COOKIE_TOKEN, token, this.domain, this.expiredTime);

            return String.format("redirect:%s", (nextUrl == null ? "/" : nextUrl));
        }
        session.setCurrentThirdpartUserId(Integer.valueOf(signupMap.get("thirdpartInfoId")));
        model.addAttribute("thirdpartEmail", signupMap.get("thirdpartEmail"));
        return BIND_EMAIL_SIGNUP_VIEM;
    }

    @RequestMapping(value = "/signup/github", method = RequestMethod.POST)
    public String signUp(@ModelAttribute(NEW_COMMAND) @Valid ThirdPartRegistrationForm form, Model model, BindingResult result) {
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                logger.info("error in register form validation: {}", error);
            }
            model.addAttribute("thirdpartEmail", form.getEmail());
            return BIND_EMAIL_SIGNUP_VIEM;
        }

        // 创建用户，并且绑定到第三方登陆上
        int id = session.getCurrentThirdpartUserId();
        accountService.thirdPartSignup(form, id);

        User user = accountService.getUserByEmailOrUsername(form.getEmail());

        session.setCurrentUser(user);

        return "redirect:/";
    }

    private void setCookie(HttpServletResponse response, String key, String value, String domain, int expiredTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiredTime);
        cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
