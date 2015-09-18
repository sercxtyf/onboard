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
package com.onboard.web.api.utils;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;

@Component("avatarUtils")
public class AvatarUtils {

    @Autowired
    private WebConfiguration configuration;

    @Autowired
    private UserService userService;

    String defaultAvatarUrl;

    Joiner joiner = Joiner.on(",");

    LoadingCache<String, String> userCache;

    @PostConstruct
    public void init() {
        defaultAvatarUrl = String.format("%s%s/avatar/default.png", configuration.getUpyunProtocol(),
                configuration.getUpyunHost());
        userCache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                User user = userService.getUserByEmail(key.split(",")[0]);

                return getAvatarByUser(user, key.split(",")[1]);
            }
        });
    }

    private String getAvatarByUser(User user, String version) {
        if (user == null) {
            return defaultAvatarUrl;
        }
        String result = String.format("%s%s%s", configuration.getUpyunProtocol(), configuration.getUpyunHost(), user.getAvatar());
        if (!Strings.isNullOrEmpty(version)) {
            result = result.concat(configuration.getUpyunSeparator()).concat(version);
        }

        return result;
    }

    public String getAvatarById(int id, String version) {
        User user = userService.getById(id);

        return getAvatarByUser(user, version);
    }

    public String getAvatarByEmail(String email, String version) {

        try {
            return userCache.get(joiner.join(email, version));
        } catch (ExecutionException e) {
            return null;
        }

    }
}
