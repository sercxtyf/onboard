package com.onboard.frontend.controller.page.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.web.SessionService;

@Controller
@RequestMapping("/account")
public class ThirdPartSigninController {

    public static final Logger logger = LoggerFactory.getLogger(ThirdPartSigninController.class);
    public static final String SIGNIN_VIEW = "account/Signin";

    @Value("${data.host}")
    private String applicationHostUrl;

    @Value("${data.github.client_id}")
    private String client_id;

    @Value("${data.github.scope}")
    private String scope;

    private static final String GITHUBOAUTH = "https://github.com/login/oauth/authorize?client_id=%s&amp;redirect_uri=%s/account/github/callback?next=%s&amp;scope=%s&amp;state=%s";

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService session;

    @Value("${data.github.state}")
    private String githubState;

    @RequestMapping(value = "/thirdpartSignin", method = RequestMethod.GET)
    public String showSignInPage(HttpServletRequest request, HttpServletResponse response) {
        String nextUrl = session.getNextUrl();
        String githubSigninCallback = String.format(GITHUBOAUTH, client_id, applicationHostUrl, nextUrl, scope, githubState);

        return "redirect:" + githubSigninCallback;
    }

}
