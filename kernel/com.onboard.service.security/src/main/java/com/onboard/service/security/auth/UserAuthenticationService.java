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
package com.onboard.service.security.auth;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;
import com.onboard.service.web.SessionService;

@Component
public class UserAuthenticationService implements UserDetailsService {
    public static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService session;

    @Override
    public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {

        User user = userService.getUserWithPasswordByEmail(arg0);
        if (user == null) {
            throw new UsernameNotFoundException("User " + arg0 + " Not Found");
        } else {
            session.setCurrentUser(user);
        }

        String salt = user.getCreated().toString();

        return new SaltedUser(arg0, user.getNewPassword(), salt, new HashSet<GrantedAuthority>());
    }
}
