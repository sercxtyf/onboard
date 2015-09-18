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
package com.onboard.service.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import com.onboard.domain.model.User;

public class WebUtils {

    public static Map<String, String> getRequestMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();

        map.put("request.servername", request.getServerName());
        map.put("request.url", request.getRequestURI());
        map.put("request.contentType", request.getContentType());
        map.put("request.contentLength", String.valueOf(request.getContentLength()));
        map.put("request.queryString", request.getQueryString());
        map.put("request.localaddr", request.getLocalAddr());
        map.put("request.localName", request.getLocalName());
        map.put("request.method", request.getMethod());

        User currentUser = (User) request.getSession().getAttribute(SessionService.CURRENT_USER);
        if (currentUser != null) {
            map.put("request.session.userName", currentUser.getName());
            map.put("request.session.userId", String.valueOf(currentUser.getId()));
            map.put("request.session.userEmail", currentUser.getEmail());
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            map.put("header-" + headerName, request.getHeader(headerName));
        }

        for (String parameter : request.getParameterMap().keySet()) {
            map.put("parameter-" + parameter, StringUtils.arrayToCommaDelimitedString(request.getParameterMap().get(parameter)));
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public static Object getPathVariableObject(HttpServletRequest request, String name) {
        Map<String, Object> pathVariables = (Map<String, Object>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null) {
            return null;
        }

        return pathVariables.get(name);
    }

    public static Integer getIntegerVariable(HttpServletRequest request, String name) {
        try {
            String s = (String) WebUtils.getPathVariableObject(request, name);

            return Integer.parseInt(s);
        } catch (Exception e) {

        }

        return null;
    }

    public static String extractPathFromPattern(final HttpServletRequest request) {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

        return finalPath;

    }

}
