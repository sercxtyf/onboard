package com.onboard.frontend.controller.page.account.form;

import com.onboard.frontend.constraints.EmailExists;

public class ForgetPasswordForm {

    @EmailExists(exist = true, message = "邮箱地址不存在")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
