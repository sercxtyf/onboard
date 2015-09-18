package com.onboard.frontend.controller.page.account.form;

import com.onboard.frontend.constraints.EmailExists;

public class SigninForm {

    @EmailExists(exist = true, message = "邮件地址不存在")
    private String email;

    private String password;

    private Boolean remember;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

}
