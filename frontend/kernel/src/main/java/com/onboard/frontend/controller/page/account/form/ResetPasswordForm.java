package com.onboard.frontend.controller.page.account.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.ScriptAssert;

@ScriptAssert(lang = "javascript",
        script = "_this.password.equals(_this.repeatPassword)",
        message = "密码必须匹配")
public class ResetPasswordForm {

    private String token;
    
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    @Length(min = 6, max = 20, message = "确认密码长度必须在6-20之间")
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
