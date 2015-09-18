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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.User;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.security.exception.NoPermissionException;

@Controller
@RequestMapping("/")
public class SigninController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public UserDTO signIn(@RequestBody User user) throws Exception {
        User currentUser = userService.login(user.getEmail(), user.getPassword());

        if (currentUser != null) {
            return UserTransform.userToUserDTO(currentUser);
        } else {
            throw new NoPermissionException("用户名或密码错误");
        }
    }

}
