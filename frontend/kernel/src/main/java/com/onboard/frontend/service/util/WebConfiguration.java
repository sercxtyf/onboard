package com.onboard.frontend.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebConfiguration {

    @Value("${site.domain}")
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
