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
package com.onboard.web.api.user;

import java.util.Map;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.onboard.domain.model.CompanyPrivilege;
import com.onboard.service.security.CompanyPrivilegeService;
import com.onboard.service.security.RoleService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.security.interceptors.CompanyOwnerRequired;
import com.onboard.service.security.interceptors.UserChecking;

/**
 * 用户权限相关操作
 * 
 * @author Dongdong Du
 * 
 */
@RequestMapping(value = "/{companyId}/users")
@Controller
public class UserPrivilegeController {
    public static final Logger logger = LoggerFactory.getLogger(UserPrivilegeController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompanyPrivilegeService companyPrivilegeService;

    @RequestMapping(value = "/{userId}/privilege", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, ?> viewUserPrivilege(@PathVariable int companyId, @PathVariable int userId) {
        boolean isUserCompanyAdmin = roleService.companyAdmin(userId, companyId);
        boolean isUserCompanyOwner = roleService.companyOwner(userId, companyId);
        return ImmutableMap.of("isUserCompanyAdmin", isUserCompanyAdmin, "isUserCompanyOwner", isUserCompanyOwner);
    }

    /**
     * 更新用户权限
     * 
     * @param companyId
     * @param userId
     * @param setComapnyAdmin
     * @return
     */
    @RequestMapping(value = "/{userId}/privilege", method = RequestMethod.POST)
    @Interceptors({ CompanyOwnerRequired.class, UserChecking.class })
    public ResponseEntity<Object> setUserPrivilege(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "setCompanyAdmin", required = true, defaultValue = "false") Boolean setComapnyAdmin) {

        CompanyPrivilege cp = companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(companyId, userId);
        cp.setCanCreateProject(setComapnyAdmin);
        cp.setIsAdmin(setComapnyAdmin);

        companyPrivilegeService.setCompanyPrivilege(cp);
        return new ResponseEntity<Object>(org.springframework.http.HttpStatus.OK);
    }

}
