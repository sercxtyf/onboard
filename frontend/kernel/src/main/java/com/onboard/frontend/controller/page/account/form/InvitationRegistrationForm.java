package com.onboard.frontend.controller.page.account.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;

import com.onboard.frontend.constraints.EmailExists;
import com.onboard.frontend.model.User;

@ScriptAssert(lang = "javascript", script = "_this.password.equals(_this.repeatPassword)", message = "密码必须匹配")
public class InvitationRegistrationForm extends User {

    /**
     * 
     */
    private static final long serialVersionUID = 124863244240405243L;

    @NotEmpty(message = "名字不能为空")
    private String name;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码必须在6-20之间")
    private String password;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码必须在6-20之间")
    private String repeatPassword;

    @Email(message = "邮箱地址不合法")
    @EmailExists(message = "邮箱地址已经存在")
    private String email;

    @Length(max = 200, message = "用户描述不能超过200个字符")
    private String description;

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
