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
package com.onboard.service.account.redis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component("luozongshuai")
public class Repository {

    public static final Logger logger = LoggerFactory.getLogger(Repository.class);

    @Autowired
    private StringRedisTemplate template;

    private ValueOperations<String, String> valueOps;

    @PostConstruct
    public void init() {
        valueOps = template.opsForValue();
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

}
