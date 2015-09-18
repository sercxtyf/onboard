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
package com.onboard.service.security.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 根据Spring MVC mapping信息中的path variable 名称获取其对应的对象
     * 
     * @param request
     * @param name
     * @return
     */
    public static Object getPathVariableObject(HttpServletRequest request, String name) {
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null) {
            return null;
        }

        return pathVariables.get(name);
    }

    /**
     * 获取path variable的整数值
     * 
     * @param request
     * @param name
     * @return 如果path variable不存在，返回null，如果不合法，返回-1
     */
    public static Integer getIntegerValueOfPathVariable(HttpServletRequest request, String name) {
        
        String value = (String) getPathVariableObject(request, name);
        
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Fail to get integer value of path variable " + name + " :", e);
        }
        return -1;
        
    }
}
