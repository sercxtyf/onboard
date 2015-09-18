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
package com.onboard.web.api.invitation;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.User;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.UserService;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.exception.InvitationTokenInvalidException;
import com.onboard.web.api.form.InvitationRegistrationForm;

@Controller
@RequestMapping("/{companyId}/invitation")
public class InvitationRegistrationOperateController {

    public static final Logger logger = LoggerFactory.getLogger(InvitationRegistrationOperateController.class);

    @Autowired
    AccountService accountService;

    @Autowired
    SessionService session;

    @Autowired
    UserService userService;

    @Autowired
    private Validator validator;

    private static final String INVITATIONRESPONSE = "invitationResponse";
    private static final String EMAILISNULL = "emailIsNull";
    private static final String TOKENINVALID = "tokenInvalid";
    private static final String USERISNULL = "userIsNull";
    private static final String USEREXIST = "userExist";

    @InitBinder
    protected void initRegistrationBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.POST)
    @ResponseBody
    public UserDTO completeRegistration(@PathVariable("companyId") int companyId, @PathVariable("token") String token,
            @Valid @RequestBody InvitationRegistrationForm form) {
        String email = accountService.authenticateInvitation(companyId, token);
        if (email == null) {
            throw new InvitationTokenInvalidException("token invalid");
        }
        accountService.completeInvitation(companyId, form, token);

        return UserTransform.userToUserDTO(userService.getById(form.getId()));
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> initRegistrationForm(@PathVariable("companyId") int companyId, @PathVariable("token") String token) {
        Map<String, String> InvitationMap = new HashMap<String, String>();

        String email = accountService.authenticateInvitation(companyId, token);
        if (email == null) {
            InvitationMap.put(INVITATIONRESPONSE, EMAILISNULL);
            return InvitationMap;
        }
        InvitationMap.put("email", email);

        User user = userService.getUserByEmail(email);

        if (user != null) {
            accountService.completeInvitation(companyId, user, token);
            InvitationMap.put(INVITATIONRESPONSE, USEREXIST);
            return InvitationMap;
        }

        InvitationMap.put(INVITATIONRESPONSE, USERISNULL);

        return InvitationMap;
    }

}
