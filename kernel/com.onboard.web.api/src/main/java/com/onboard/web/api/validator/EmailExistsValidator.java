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
import com.onboard.web.api.constraints.EmailExists;

public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {

    public static final Logger logger = LoggerFactory.getLogger(EmailExistsValidator.class);

    @Autowired
    private UserService userService;

    private boolean exist;

    @Override
    public void initialize(EmailExists arg0) {
        this.exist = arg0.exist();
    }

    @Override
    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        return !(this.exist ^ userService.isEmailRegistered(arg0));
    }

}
