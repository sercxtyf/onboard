package com.onboard.frontend.interceptors;

import com.onboard.frontend.service.web.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 判断是否登录的Spring MVC Interceptor
 * 
 * @author SERC
 * 
 */
@Component
public class LoginRequired extends HandlerInterceptorAdapter {

    @Autowired
    protected SessionService session;

    public static final Logger logger = LoggerFactory.getLogger(LoginRequired.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (session.getCurrentUser() == null) {
            if (request.getRequestURI().startsWith("/api")) {
                response.setContentType("application/json;charset=UTF-8");
                String status = "{\"status\" : \"loginRequired\",";
                status += "\"next\":" + "\"/account/signin?next=" + request.getHeader("Referer") + "\"}";
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(status);
                response.getWriter().flush();
            } else {
                response.sendRedirect("/account/signin?next=" + request.getRequestURL().toString());
            }
            return false;
        }

        return super.preHandle(request, response, handler);
    }
}
