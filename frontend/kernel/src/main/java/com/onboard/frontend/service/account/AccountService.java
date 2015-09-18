package com.onboard.frontend.service.account;

import java.util.Map;

import com.onboard.frontend.controller.page.account.form.ForgetPasswordForm;
import com.onboard.frontend.controller.page.account.form.RegistrationForm;
import com.onboard.frontend.controller.page.account.form.ResetPasswordForm;
import com.onboard.frontend.controller.page.account.form.ThirdPartRegistrationForm;
import com.onboard.frontend.model.User;

/**
 * Created by XingLiang on 2015/4/23.
 */
public interface AccountService {

    String AUTHENTICATE_USER = "/signin";

    String GET_USER_BY_ID = "/users/%d";

    String GET_USER_BY_EMAIL = "/users?email-or-username=%s";

    String FORGET_PASSWORD = "/account-forget";

    String RESET_PASSWORD = "/account-reset/%d/token/%s";

    String ACCOUNT_CONFIRM = "/account-confirm/%d/token/%s";

    String SIGN_UP = "/signup";

    String THIRD_PART_SIGN_UP = "/signup/github/callback?code=%s";

    String THIRD_PART_SIGN_IN = "/signin/github/callback?code=%s";

    String THIRD_PART_SIGN_UP_POST = "/signup/github?id=%s";

    String AUTHENTICATE_USER_WITH_THIRDPART_USER = "/signin?thirdpartUserId=%s";

    /**
     * 通过id获取用户
     * 
     * @param id
     * @return
     */
    User getUserById(int id);

    /**
     * 根据用户邮箱或用户名获取用户
     * 
     * @param email
     * @return
     */
    User getUserByEmailOrUsername(String emailOrUsername);

    /**
     * 验证用户身份, 如成功返回用户详细信息
     * 
     * @param user
     * @return
     */
    User authenticateUser(User user);

    /**
     * 获取remember me功能的token
     * 
     * @param uid
     * @return
     * @throws Exception
     */
    String getRememberMeToken(int uid);

    /**
     * 验证remeberMe功能的token，如果成功则返回用户对象
     * 
     * @param token
     * @return user
     * @throws Exception
     */
    User authenticateRememberMeToken(int uid, String token);

    /**
     * 删除RememberMe Token，用于用户登出
     * 
     * @param uid
     */
    void deleteRememberMeToken(int uid);

    /**
     * 验证忘记密码功能的token
     * 
     * @param token
     * @return
     * @throws Exception
     */
    boolean authenticateForgetToken(int uid, String token);

    /**
     * 忘记密码
     * 
     * @param email
     */
    boolean forgetPassword(ForgetPasswordForm forgetPasswordForm);

    /**
     * 重置密码
     * 
     * @param password
     * @param token
     */
    boolean resetPassword(int uid, String token, ResetPasswordForm resetPasswordForm);

    /**
     * 确认用户注册信息
     * 
     * @param token
     */
    boolean confirmRegisteredUser(int uid, String token);

    /**
     * 注册用户
     * 
     * @param registrationForm
     * @return
     */
    boolean signup(RegistrationForm registrationForm);

    User thirdPartAuthenticateUser(String code);

    Map<String, String> thirdPartSignupCallback(String code);

    boolean thirdPartSignup(ThirdPartRegistrationForm thirdPartRegistrationForm, Integer id);

    User authenticateUserAndBandTheThirdpardUser(User user, int thirdpartUserId);

}
