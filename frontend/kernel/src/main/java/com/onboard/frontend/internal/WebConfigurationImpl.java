package com.onboard.frontend.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by XingLiang on 2015/4/23.
 */

@Service("webConfigurationBean")
public class WebConfigurationImpl {

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
