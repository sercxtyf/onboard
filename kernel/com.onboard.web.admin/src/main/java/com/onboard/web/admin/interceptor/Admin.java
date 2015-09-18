package com.onboard.web.admin.interceptor;

import com.google.common.primitives.Ints;
import com.onboard.service.web.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component("adminInterceptor")
public class Admin extends HandlerInterceptorAdapter {

    private static final List<Integer> adminIds = Ints.asList(-1, 1, 2);

    @Autowired
    private SessionService session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return session.getCurrentUser() != null && adminIds.contains(session.getCurrentUser().getId());
    }
}
