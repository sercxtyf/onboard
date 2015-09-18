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

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * custom cacheManager for fluzzy cache evict
 * 
 * @author XingLiang
 * 
 */
public class OnboardCacheManagerImpl extends RedisCacheManager implements CacheManager {

    @SuppressWarnings("rawtypes")
    private final RedisTemplate template;

    public OnboardCacheManagerImpl(@SuppressWarnings("rawtypes") RedisTemplate template) {
        super(template);
        this.template = template;
    }

    @Override
    public Cache getCache(String name) {
        Cache redisCache = super.getCache(name);

        return new RedisCacheDecorator(redisCache, template, name);
    }
}
