package com.onboard.frontend.controller.page.account.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.web.multipart.MultipartFile;

import com.onboard.frontend.constraints.Password;
import com.onboard.frontend.constraints.Username;
import com.onboard.frontend.constraints.UsernameExists;
import com.onboard.frontend.model.User;

@ScriptAssert(lang = "javascript", script = "_this.password.equals(_this.repeatPassword)", message = "密码必须匹配")
public class UserUpdateForm extends User {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Length(min = 1, max = 20, message = "昵称必须在1-20之间")
    @NotBlank(message = "名字不能为空")
    private String name;

    @Username(message = "用户名只能包含字母和下划线\n长度必须在3-16之间")
    @UsernameExists(message = "用户名已存在")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Length(max = 200, message = "描述不能超过200")
    private String description;

    @Password(message = "密码必须在6-20之间")
    private String password;

    private String repeatPassword;

    private MultipartFile avatarFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public MultipartFile getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(MultipartFile avatarFile) {
        this.avatarFile = avatarFile;
    }

}
