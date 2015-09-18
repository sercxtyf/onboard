package com.onboard.frontend.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by XingLiang on 2015/4/28.
 */
@Component
public class Repository {

    private StringRedisTemplate template;

    private ValueOperations<String, String> valueOps;

    @Autowired
    public Repository(StringRedisTemplate template) {
        this.template = template;
        this.valueOps = template.opsForValue();
    }

    public String addToken(TokenType type, int uid, int timeout) {
        String token = UUID.randomUUID().toString();
        valueOps.set(KeyUtils.userToken(type.getName(), uid), token, timeout, TimeUnit.SECONDS);
        return token;
    }

    public boolean authenticateToken(TokenType type, int uid, String token) {
        String value = valueOps.get(KeyUtils.userToken(type.getName(), uid));
        return value != null && value.equals(token);
    }

    public void delToken(TokenType type, int uid) {
        template.delete(KeyUtils.userToken(type.getName(), uid));
    }

    public void saveIntData(String key, int value) {
        valueOps.set(key, String.valueOf(value));
    }

    public Integer getIntData(String key, int defaultValue) {
        String valueString = valueOps.get(key);
        try {
            return Integer.valueOf(valueString);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void removeIntData(String key) {
        template.delete(key);
    }
}
