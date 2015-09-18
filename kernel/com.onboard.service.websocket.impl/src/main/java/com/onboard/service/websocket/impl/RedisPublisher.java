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
package com.onboard.service.websocket.impl;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.common.identifiable.IdentifiableManager;

/**
 * @author Gourui
 * 
 */
@Service("redisPublisherBean")
public class RedisPublisher implements ActivityHook {

    public static final Logger logger = LoggerFactory.getLogger(RedisPublisher.class);

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private IdentifiableManager identifiableManager;

    private void broadcastByActivity(User owner, Activity activity, BaseProjectItem originalItem, BaseProjectItem updatedItem)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        activity.setSubscribers(userService.getUserByProjectId(activity.getProjectId()));
        activity.setAttachObject((BaseProjectItem)identifiableManager.getIdentifiableByTypeAndId(activity.getAttachType(),
                activity.getAttachId()));
        String message = "";
        try {
            message = ow.writeValueAsString(activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        template.convertAndSend("channel", message);
    }

    @Override
    public void whenCreationActivityCreated(User owner, Activity activity, BaseProjectItem item) throws Throwable {
        broadcastByActivity(owner, activity, item, item);
    }

    @Override
    public void whenUpdateActivityCreated(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem)
            throws Throwable {
        broadcastByActivity(owner, activity, item, updatedItem);
    }
}
