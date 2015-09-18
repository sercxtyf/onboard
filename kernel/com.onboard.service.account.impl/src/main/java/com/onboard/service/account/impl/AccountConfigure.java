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
package com.onboard.service.account.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountConfigure {

    private String protocol = "https://";

    @Value("${account.tokenExpired}")
    private int tokenExpired;

    @Value("${account.rememberMeExpired}")
    private int rememberMeExpired;

    @Value("${site.host}")
    private String host;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(int tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public int getRememberMeExpired() {
        return rememberMeExpired;
    }

    public void setRememberMeExpired(int rememberMeExpired) {
        this.rememberMeExpired = rememberMeExpired;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
