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
package com.onboard.web.api.exception;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class HttpStatusHandlerExceptionResolver implements HandlerExceptionResolver {

    private Map<String, Integer> exceptionStatusMapping;

    private Integer defaultStatusCode = 500;

    public void setDefaultStatusCode(int defaultStatusCode) {
        this.defaultStatusCode = defaultStatusCode;
    }

    public void setExceptionStatusMapping(Properties mappings) {
        if (exceptionStatusMapping == null) {
            exceptionStatusMapping = new HashMap<String, Integer>();
        }
        for (Enumeration<?> enumeration = mappings.propertyNames(); enumeration.hasMoreElements();) {
            String exception = (String) enumeration.nextElement();
            Integer statusCode = new Integer(mappings.getProperty(exception));
            this.exceptionStatusMapping.put(exception, statusCode);
        }
    }

    protected int getDepth(String exceptionMapping, Exception ex) {
        return getDepth(exceptionMapping, ex.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    protected Integer findMatchingstatusCode(Exception ex) {
        Integer statusCode = null;
        int deepest = Integer.MAX_VALUE;
        for (String exceptionName : exceptionStatusMapping.keySet()) {
            int depth = getDepth(exceptionName, ex);
            if (depth >= 0 && depth < deepest) {
                deepest = depth;
                statusCode = exceptionStatusMapping.get(exceptionName);
            }
        }
        return statusCode;
    }

    protected Integer determineStatusCode(Exception ex, HttpServletRequest request) {
        Integer statusCode = null;
        // Check for specific exception mappings.
        if (this.exceptionStatusMapping != null) {
            statusCode = findMatchingstatusCode(ex);
        }
        // Return default error view else, if defined.
        if (statusCode == null && this.defaultStatusCode != null) {
            statusCode = this.defaultStatusCode;
        }
        return statusCode;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Integer statusCode = determineStatusCode(ex, request);
        if (statusCode != null) {
            if (statusCode != null) {
                try {
                    response.sendError(statusCode);
                    return new ModelAndView();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
