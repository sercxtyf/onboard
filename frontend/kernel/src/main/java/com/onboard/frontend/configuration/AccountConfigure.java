package com.onboard.frontend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by XingLiang on 2015/4/28.
 */
@Service
public class AccountConfigure {

    @Value("${site.protocol}")
    private String protocol;

    @Value("${account.tokenExpired}")
    private int tokenExpired;

    @Value("${account.rememberMeExpired}")
    private int rememberMeExpired;

    @Value("${site.domain}")
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
