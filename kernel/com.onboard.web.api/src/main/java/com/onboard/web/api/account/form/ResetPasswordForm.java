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
