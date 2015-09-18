package com.onboard.frontend.service.util;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.account.AccountService;

@Component("avatarUtils")
public class AvatarUtils {

    @Autowired
    private WebConfiguration configuration;

    @Autowired
    private AccountService accountService;

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
                User user = accountService.getUserByEmailOrUsername(key.split(",")[0]);

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
        User user = accountService.getUserById(id);

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
