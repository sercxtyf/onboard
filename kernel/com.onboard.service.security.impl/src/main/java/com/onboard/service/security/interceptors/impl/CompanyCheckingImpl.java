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

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.service.account.CompanyService;
import com.onboard.service.security.interceptors.BasicIdentifiableInterceptor;
import com.onboard.service.security.interceptors.CompanyChecking;
import com.onboard.service.security.utils.SecurityUtils;

@Service("companyCheckingBean")
public class CompanyCheckingImpl extends BasicIdentifiableInterceptor implements CompanyChecking {

    @Autowired
    CompanyService companyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!super.preHandle(request, response, handler)) {
            return false;
        }

        Integer companyId = SecurityUtils.getIntegerValueOfPathVariable(request, "companyId");
        if (companyId == null || companyId < 0 || companyService.getById(companyId) == null) {
            throw new ResourceNotFoundException();
        }

        return true;
    }
}
