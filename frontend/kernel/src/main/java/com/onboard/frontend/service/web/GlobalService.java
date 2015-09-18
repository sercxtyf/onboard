package com.onboard.frontend.service.web;

import javax.servlet.http.HttpSession;

/**
 * Created by XingLiang on 2015/4/23.
 */
public interface GlobalService {

    public HttpSession getSession();

    public void set(String key, Object object);

    public Object get(String key);
}
