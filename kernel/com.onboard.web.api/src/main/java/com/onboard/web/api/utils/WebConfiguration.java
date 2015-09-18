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
package com.onboard.web.api.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("webConfigurationBean")
public class WebConfiguration {

    @Value("${site.host}")
    private String host;

    @Value("${site.static.host}")
    private String staticHost;

    @Value("${site.static.protocol}")
    private String staticProtocol;

    public String getHost() {
        return host;
    }

    public String getStaticHost() {
        return staticHost;
    }

    public String getStaticProtocol() {
        return staticProtocol;
    }

    public String getUpyunHost() {
        return "teamforge.b0.upaiyun.com";
    }

    public String getUpyunProtocol() {
        return "https://";
    }

    public String getUpyunSeparator() {
        return "!";
    }

    public String getServerEnvName() {
        return "onboard";
    }

}
