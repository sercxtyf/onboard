package com.onboard.frontend.controller.page.account;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.interceptors.RememberMeInterceptor;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.web.SessionService;

/**
 * Created by XingLiang on 2015/4/23.
 */

@Controller
@RequestMapping("/account")
public class LoginController {

    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    public static final String SIGNIN_VIEW = "account/Signin";
    private static final String BIND_EMAIL_SIGNUP_VIEM = "account/SignupBindEmail";

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

    @Value("${data.github.state}")
    private String githubState;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String signIn(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nextUrl = request.getParameter("next");

        logger.debug("Current Thread Class Loader is {}", Thread.currentThread().getContextClassLoader());
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        User returnedUser = null;
        // 绑定已有的账户
        int thirdpartUserId = sessionService.getCurrentThirdpartUserId();
        if (thirdpartUserId != -1) {
            returnedUser = accountService.authenticateUserAndBandTheThirdpardUser(user, thirdpartUserId);
            if (returnedUser == null) {
                model.addAttribute("loginError", true);
                return BIND_EMAIL_SIGNUP_VIEM;
            }
        } else {
            returnedUser = accountService.authenticateUser(user);
        }

        if (returnedUser == null) {
            model.addAttribute("loginError", true);
            return SIGNIN_VIEW;
        }
        returnedUser.setPassword(password);
        sessionService.setCurrentUser(returnedUser);
        String token = accountService.getRememberMeToken(returnedUser.getId());
        setCookie(response, RememberMeInterceptor.COOKIE_UID, String.valueOf(returnedUser.getId()), this.domain, this.expiredTime);
        setCookie(response, RememberMeInterceptor.COOKIE_TOKEN, token, this.domain, this.expiredTime);

        return String.format("redirect:%s", (nextUrl == null ? "/" : nextUrl));
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String signout(HttpServletResponse response) {
        User user = sessionService.getCurrentUser();
        if (user != null) {
            accountService.deleteRememberMeToken(user.getId());
            sessionService.removeUserInformation();
        }
        setCookie(response, RememberMeInterceptor.COOKIE_UID, null, this.domain, 0);
        setCookie(response, RememberMeInterceptor.COOKIE_TOKEN, null, this.domain, 0);
        return "redirect:/";
    }

    /**
     * 用户登录
     * 
     * @return
     */
    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String showSignInPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        String nextUrl = request.getParameter("next");

        if (nextUrl == null || nextUrl.length() != 0) {
            sessionService.setCurrentNextUrl(nextUrl);
        }
        if (sessionService.getCurrentUser() != null) {
            return "redirect:/";
        }

        return SIGNIN_VIEW;
    }

    private void setCookie(HttpServletResponse response, String key, String value, String domain, int expiredTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiredTime);
        cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
