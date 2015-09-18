package com.onboard.frontend.service.account.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.frontend.configuration.AccountConfigure;
import com.onboard.frontend.controller.page.account.form.ForgetPasswordForm;
import com.onboard.frontend.controller.page.account.form.RegistrationForm;
import com.onboard.frontend.controller.page.account.form.ResetPasswordForm;
import com.onboard.frontend.controller.page.account.form.ThirdPartRegistrationForm;
import com.onboard.frontend.model.ResponseMap;
import com.onboard.frontend.model.User;
import com.onboard.frontend.redis.Repository;
import com.onboard.frontend.redis.TokenType;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.net.impl.NetServiceImpl;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Service
public class AccountServiceImpl implements AccountService {

    public static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountConfigure configurer;

    @Autowired
    private NetServiceImpl netService;

    @Autowired
    private Repository repository;

    public User getUserById(int id) {
        User result = null;
        String uri = String.format(GET_USER_BY_ID, id);
        try {
            result = netService.getForObject(uri, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public User authenticateUser(User user) {
        User result = null;
        try {
            result = netService.postForFormObject(AUTHENTICATE_USER, user, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getRememberMeToken(int uid) {
        return repository.addToken(TokenType.REMEMBER_ME, uid, configurer.getRememberMeExpired());
    }

    public User authenticateRememberMeToken(int uid, String token) {
        return repository.authenticateToken(TokenType.REMEMBER_ME, uid, token) ? getUserById(uid) : null;
    }

    public void deleteRememberMeToken(int uid) {
        repository.delToken(TokenType.REMEMBER_ME, uid);
    }

    public boolean authenticateForgetToken(int uid, String token) {
        return repository.authenticateToken(TokenType.FORGET_PASSWORD, uid, token);
    }

    public User getUserByEmailOrUsername(String emailOrUsername) {
        User result = null;
        String uri = String.format(GET_USER_BY_EMAIL, emailOrUsername);
        try {
            result = netService.getForObject(uri, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean forgetPassword(ForgetPasswordForm forgetPasswordForm) {
        Boolean result = null;
        try {
            result = netService.postForFormObject(FORGET_PASSWORD, forgetPasswordForm, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean resetPassword(int uid, String token, ResetPasswordForm resetPasswordForm) {
        Boolean result = null;
        String uri = String.format(RESET_PASSWORD, uid, token);
        try {
            result = netService.postForFormObject(uri, resetPasswordForm, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean confirmRegisteredUser(int uid, String token) {
        Boolean result = null;
        String uri = String.format(ACCOUNT_CONFIRM, uid, token);
        try {
            result = netService.postForFormObject(uri, new Object(), Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean signup(RegistrationForm registrationForm) {
        Boolean result = null;
        try {
            result = netService.postForFormObject(SIGN_UP, registrationForm, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public User thirdPartAuthenticateUser(String code) {
        User user = netService.getForObject(String.format(THIRD_PART_SIGN_IN, code), User.class);
        return user;
    }

    public Map<String, String> thirdPartSignupCallback(String code) {
        return netService.getForObject(String.format(THIRD_PART_SIGN_UP, code), ResponseMap.class);
    }

    public boolean thirdPartSignup(ThirdPartRegistrationForm thirdPartRegistrationForm, Integer id) {
        return netService.postForFormObject(String.format(THIRD_PART_SIGN_UP_POST, id), thirdPartRegistrationForm, Boolean.class);
    }

    public User authenticateUserAndBandTheThirdpardUser(User user, int thirdpartUserId) {
        User result = null;
        try {
            result = netService.postForFormObject(String.format(AUTHENTICATE_USER_WITH_THIRDPART_USER, thirdpartUserId), user,
                    User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
