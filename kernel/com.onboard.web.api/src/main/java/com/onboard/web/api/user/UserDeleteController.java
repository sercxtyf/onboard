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

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.service.account.CompanyService;
import com.onboard.service.security.interceptors.CompanyAdminRequired;
import com.onboard.service.security.interceptors.UserChecking;

/**
 * 删除用户
 * 
 * @author xuchen
 * 
 */
@RequestMapping(value = "/{companyId}/users")
@Controller
public class UserDeleteController {

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @Interceptors({ CompanyAdminRequired.class, UserChecking.class })
    public String removeUserFromCompany(@PathVariable int companyId, @PathVariable int userId) {
        companyService.removeUser(companyId, userId);
        return "redirect:/{companyId}/users";
    }

}
