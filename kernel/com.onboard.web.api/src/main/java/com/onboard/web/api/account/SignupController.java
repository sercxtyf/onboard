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
package com.onboard.web.api.account;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.User;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.sampleProject.SampleProjectService;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.account.form.RegistrationForm;

@Controller
@RequestMapping("/")
public class SignupController {

    public static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SampleProjectService sampleProjectService;

    /**
     * 注册用户
     * 
     * @param form
     *            用户提交的注册表单
     * @param result
     *            表单绑定结果，用于判定表单是否验证通过
     * 
     * @return
     */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public Boolean signUp(@Valid @RequestBody RegistrationForm form) {
        logger.info("start");

        userService.signUp(form, form.getCompanyName());

        User user = userService.getUserByEmail(form.getEmail());
        sessionService.setCurrentUser(user);
        int companyId = companyService.getCompaniesByUserId(user.getId()).get(0).getId();
        sampleProjectService.createSampleProjectByCompanyId(companyId, user);

        return true;
    }
}
