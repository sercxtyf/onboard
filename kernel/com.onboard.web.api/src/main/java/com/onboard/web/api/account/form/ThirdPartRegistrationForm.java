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

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.onboard.domain.model.User;
import com.onboard.web.api.constraints.EmailExists;

public class ThirdPartRegistrationForm extends User {

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

}
