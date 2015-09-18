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

import com.onboard.domain.model.User;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.security.interceptors.BasicIdentifiableInterceptor;
import com.onboard.service.security.interceptors.UserChecking;
import com.onboard.service.security.utils.SecurityUtils;

/**
 * 判断user是否存在，以及user是否属于公司
 * 
 * @author yewei
 * 
 */
@Service("userCheckingBean")
public class UserCheckingImpl extends BasicIdentifiableInterceptor implements UserChecking {

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!super.preHandle(request, response, handler)) {
            return false;
        }

        Integer userId = SecurityUtils.getIntegerValueOfPathVariable(request, "userId");
        User user = userService.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        Integer companyId = SecurityUtils.getIntegerValueOfPathVariable(request, "companyId");
        if (companyId == null || companyId < 0 || !companyService.containsUser(companyId, userId)) {
            throw new NoPermissionException(companyId);
        }

        return true;
    }
}
