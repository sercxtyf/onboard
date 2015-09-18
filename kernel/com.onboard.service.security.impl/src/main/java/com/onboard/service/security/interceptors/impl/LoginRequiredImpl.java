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
package com.onboard.service.security.interceptors.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.onboard.service.security.interceptors.LoginRequired;
import com.onboard.service.web.SessionService;

/**
 * 判断是否登录的Spring MVC Interceptor
 * 
 * @author SERC
 * 
 */

@Service("loginRequiredBean")
public class LoginRequiredImpl extends HandlerInterceptorAdapter implements LoginRequired {

    @Autowired
    protected SessionService session;

    public static final Logger logger = LoggerFactory.getLogger(LoginRequiredImpl.class);

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
