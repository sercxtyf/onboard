package com.onboard.frontend.controller.page.account.form;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.onboard.frontend.constraints.EmailExists;
import com.onboard.frontend.model.User;

public class ThirdPartRegistrationForm extends User {

    /**
     * 
     */
    private static final long serialVersionUID = 8614575506080939245L;

    @Length(min = 1, max = 20, message = "用户名必须在1-20之间")
    @NotBlank(message = "名字不能为空")
    private String name;

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
