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
package com.onboard.service.common.cache.impl;

import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis cache decorator for fluzzy evict.
 * 
 * @author XingLiang
 * 
 */

@SuppressWarnings("unchecked")
public class RedisCacheDecorator implements Cache {

    private Cache cache;

    private byte[] setName;

    private String name;

    @SuppressWarnings("rawtypes")
    private final RedisTemplate template;

    @SuppressWarnings("rawtypes")
    public RedisCacheDecorator(Cache cache, RedisTemplate template, String name) {
        this.cache = cache;
        this.template = template;
        this.name = name;
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        this.setName = stringSerializer.serialize(name + "~keys");
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @SuppressWarnings("rawtypes")
    public RedisTemplate getTemplate() {
        return template;
    }

    public String getName() {
        return cache.getName();
    }

    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    private String getKey(Object key) {
        return name + ":" + key;
    }

    public ValueWrapper get(final Object key) {
        return cache.get(getKey(key));
    }

    public void put(final Object key, final Object value) {
        cache.put(getKey(key), value);
    }

    public void evict(Object originKey) {
        String key = getKey(originKey);
        String keyString = "" + key;
        if (keyString.contains("*")) {
            String keyPattern = "*" + keyString;
            final byte[] k = template.getStringSerializer().serialize(keyPattern);
            template.execute(new RedisCallback<Object>() {
                public Object doInRedis(RedisConnection connection) {
                    Set<byte[]> keys = connection.keys(k);
                    for (byte[] currentKey : keys) {
                        connection.del(currentKey);
                        connection.zRem(setName, currentKey);
                    }
                    return null;
                }
            }, true);
        } else {
            cache.evict(key);
        }
    }

    public void clear() {
        cache.clear();
    }
}
