package com.onboard.frontend.controller.page.account.form;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;

import com.onboard.frontend.constraints.EmailExists;
import com.onboard.frontend.model.User;

@ScriptAssert(lang = "javascript", script = "_this.password.equals(_this.repeatPassword)", message = "密码必须匹配")
public class RegistrationForm extends User {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Length(min = 1, max = 20, message = "用户名必须在1-20之间")
    @NotBlank(message = "名字不能为空")
    private String name;

    @Length(min = 6, max = 20, message = "密码必须在6-20之间")
    private String password;

    private String repeatPassword;

    @Email(message = "邮箱地址不合法")
    @EmailExists(message = "邮箱地址已经存在")
    @Length(max = 50, message = "邮箱长度不能超过50")
    @NotBlank
    private String email;

    @NotBlank(message = "公司名称不能为空")
    @Length(min = 1, max = 50, message = "公司名称长度必须在1-50之间")
    private String companyName;

    @AssertTrue(message = "请认真阅读并同意用户协议")
    private Boolean agree;

    // @NotBlank(message = "邀请Token不能为空")
    private String trialToken;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public String getTrialToken() {
        return trialToken;
    }

    public void setTrialToken(String trialToken) {
        this.trialToken = trialToken;
    }
}
