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

import java.io.IOException;
import java.util.Map;

import javax.validation.Valid;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;
import com.onboard.service.security.interceptors.LoginRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.account.form.ForgetPasswordForm;
import com.onboard.web.api.account.form.ResetPasswordForm;
import com.onboard.web.api.account.form.UserUpdateForm;
import com.onboard.web.api.utils.WebConfiguration;

@Controller
@RequestMapping("/")
public class AccountApiController {

    public static final Logger logger = LoggerFactory.getLogger(AccountApiController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService session;

    @Autowired
    private WebConfiguration configuration;

    /**
     * 忘记密码
     */
    @RequestMapping(value = "/account-forget", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void forgetPassword(@Valid @RequestBody ForgetPasswordForm form) {

        userService.forgetPassword(form.getEmail());
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "/account-reset/{uid}/token/{token}", method = RequestMethod.POST)
    @ResponseBody
    public Boolean resetPassword(@PathVariable("uid") int uid, @PathVariable("token") String token,
            @RequestBody @Valid ResetPasswordForm form) {
        if (!userService.resetPassword(uid, form.getPassword(), token)) {
            return false;
        }

        return true;
    }

    /**
     * 确认注册
     */
    @RequestMapping(value = "/account-confirm/{uid}/token/{token}", method = RequestMethod.POST)
    @ResponseBody
    public Boolean confirmUser(@PathVariable("uid") int uid, @PathVariable("token") String token) {

        boolean result = userService.confirmRegisteredUser(uid, token);
        if (!result) {
            return false;
        }
        return true;
    }

    /**
     * 更新用户信息
     */
    @RequestMapping(value = "/account", method = RequestMethod.POST)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateForm form) {
        User user = session.getCurrentUser();
        logger.debug("exist username is " + user.getUsername());
        logger.debug("form username is " + form.getUsername());

        if (user.getUsername() != null && user.getUsername().length() > 0) {
            // From 邢亮，软件学院那边用户名不可修改。当用户名已经存在时，不能更新
            form.setUsername(null);
        }
        form.setId(user.getId());
        form.setEmail(null);
        userService.updateUser(form, null, null);
        form.setEmail(user.getEmail());

        session.setCurrentUser(userService.getById(user.getId()));

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account-password", method = RequestMethod.POST)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public ResponseEntity<Object> updateUserPassword(@RequestBody UserUpdateForm form) {
        if (userService.login(session.getCurrentUser().getEmail(), form.getPassword()) == null) {
            return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        }
        User user = new User();
        user.setId(session.getCurrentUser().getId());
        user.setNewPassword(form.getNewPassword());
        userService.updateUser(user, null, null);

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account-avatar", method = RequestMethod.POST)
    @Interceptors({ LoginRequired.class })
    public ResponseEntity<Map<String, String>> updateUserAvatar(@RequestParam("avatar") MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return new ResponseEntity<Map<String, String>>(HttpStatus.BAD_REQUEST);
        }
        User user = new User(session.getCurrentUser().getId());
        try {
            userService.updateUser(user, avatarFile.getBytes(), avatarFile.getOriginalFilename());
            session.setCurrentUser(userService.getById(user.getId()));
        } catch (IOException e) {
            return new ResponseEntity<Map<String, String>>(HttpStatus.BAD_REQUEST);
        }

        String avatarUrl = String.format("%s/%s/%s%s%s", configuration.getUpyunProtocol(), configuration.getUpyunHost(), session
                .getCurrentUser().getAvatar(), configuration.getUpyunSeparator(), "avatar110");
        return new ResponseEntity<Map<String, String>>(ImmutableMap.of("url", avatarUrl), HttpStatus.OK);
    }

    @RequestMapping(value = "/account-exist", method = RequestMethod.GET)
    @ResponseBody
    public Boolean getUserByEmailOrUsername(@RequestParam(value = "email-or-username", required = true) String emailOrUsername) {
        User user = userService.getUserByEmailOrUsername(emailOrUsername);
        if (null == user) {
            return false;
        }
        return true;
    }

}
