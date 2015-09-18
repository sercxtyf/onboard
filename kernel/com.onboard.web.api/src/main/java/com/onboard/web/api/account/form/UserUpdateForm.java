/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.web.api.account.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.web.multipart.MultipartFile;

import com.onboard.domain.model.User;
import com.onboard.web.api.constraints.Password;
import com.onboard.web.api.constraints.Username;
import com.onboard.web.api.constraints.UsernameExists;

@ScriptAssert(lang = "javascript", script = "_this.password.equals(_this.repeatPassword)", message = "密码必须匹配")
public class UserUpdateForm extends User {

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
