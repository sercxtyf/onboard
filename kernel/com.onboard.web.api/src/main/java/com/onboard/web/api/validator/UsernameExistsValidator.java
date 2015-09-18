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
package com.onboard.web.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.onboard.service.account.UserService;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.constraints.UsernameExists;

public class UsernameExistsValidator implements ConstraintValidator<UsernameExists, String> {

    public static final Logger logger = LoggerFactory.getLogger(EmailExistsValidator.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    private boolean exist;

    @Override
    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        String currentUserUsername = sessionService.getCurrentUser().getUsername();
        if (currentUserUsername != null && arg0.equals(currentUserUsername)) {
            return true;
        }
        return !(this.exist ^ userService.containUsername(arg0));
    }

    @Override
    public void initialize(UsernameExists constraintAnnotation) {
        this.exist = constraintAnnotation.exist();
    }
}
