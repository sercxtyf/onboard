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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.CompanyApplication;
import com.onboard.domain.model.User;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.CompanyApplicationService;
import com.onboard.service.sampleProject.SampleProjectService;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.account.form.RegistrationForm;

@Controller
@RequestMapping("/account/trial")
public class TrialSignupController {
    public static final Logger logger = LoggerFactory.getLogger(TrialSignupController.class);

    private static final String NEW_COMMAND = "newCommand";

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService session;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyApplicationService companyApplicationService;

    @Autowired
    private SampleProjectService sampleProjectService;

    @ModelAttribute(NEW_COMMAND)
    public RegistrationForm getUserRegistrationForm() {
        return new RegistrationForm();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public CompanyApplication initRegistrationForm(@RequestParam("token") String token) {
        CompanyApplication application = companyApplicationService.getCompanyApplicationByToken(token);

        return application;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Boolean signUp(@ModelAttribute(NEW_COMMAND) @Valid RegistrationForm form, BindingResult result) {
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                logger.warn("this is a hack request: {}", error);
            }
            return false;
        }
        CompanyApplication application = companyApplicationService.getCompanyApplicationByToken(form.getTrialToken());
        if (application == null) {
            return false;
        }

        userService.signUp(form, form.getCompanyName());

        companyApplicationService.disableCompanyApplicationToken(application.getId());
        User user = userService.getUserByEmail(form.getEmail());
        session.setCurrentUser(user);
        int companyId = companyService.getCompaniesByUserId(user.getId()).get(0).getId();
        sampleProjectService.createSampleProjectByCompanyId(companyId, user);

        return true;
    }
}
