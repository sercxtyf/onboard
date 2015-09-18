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
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.catalina.websocket.MessageInbound;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Notification;
import com.onboard.domain.model.User;
import com.onboard.service.websocket.WebSocketService;

@Service("webSocketServiceBean")
public class WebSocketServiceImpl implements WebSocketService {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    private Multimap<String, MessageInbound> userPagesMap;

    @PostConstruct
    public void init() {
        userPagesMap = ArrayListMultimap.create();
    }

    @Override
    public void registerClient(String user, MessageInbound inbound) {
        logger.info("New page registered at" + new Date().toString());
        userPagesMap.put(user, inbound);
        logger.info("Size of " + user + ":" + userPagesMap.get(user).size());
    }

    @Override
    public void unregisterClient(String user, MessageInbound inbound) {
        logger.info("A page unregistered at " + new Date().toString());
        userPagesMap.remove(user, inbound);
        logger.info("Size of " + user + ":" + userPagesMap.get(user).size());
    }

    @Override
    public void sendToPage(MessageInbound inbound, String message) throws IOException {
        inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
        logger.info("message sent to client:" + message);
    }

    /**
     * 根据map生成json
     * 
     * @param actionMap
     * @return
     */
    private String getJson(Object activity) {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String message = "";
        try {
            message = ow.writeValueAsString(activity);
        } catch (IOException e) {
            logger.info("activity to json failure");
        }
        return message;
    }

    @Override
    /**
     * code be strictly written like below(about synchronization), following guava's official guide. 
     */
    public void broadcastOne(String user, Activity activity) {
        logger.info(getJson(activity));
        String message = getJson(activity);
        broadcastOne(user, message);
    }

    @Override
    public void broadcastOne(String user, Notification notification) {
        String message = getJson(notification);
        broadcastOne(user, message);
    }

    @Override
    public void broadcastAll(Activity activity) {
        Set<String> keySet = userPagesMap.keySet();
        for (String user : keySet) {
            broadcastOne(user, activity);
        }
    }

    @Override
    public void broadcastOne(String user, String message) {
        Multimap<String, MessageInbound> syncMap = Multimaps.synchronizedMultimap(userPagesMap);
        Collection<MessageInbound> mis = syncMap.get(user);
        synchronized (syncMap) {
            if (mis != null) {
                Iterator<MessageInbound> it = mis.iterator();
                while (it.hasNext()) {
                    MessageInbound inbound = it.next();
                    try {
                        sendToPage(inbound, message);
                    } catch (IOException e) {
                        // userPagesMap.remove(user, inbound);
                        logger.info("The WebSocket connection has been closed: " + inbound.toString());
                    }

                }
            }
        }
    }

    @Override
    public void broadcastOne(User user, Object object) {
        broadcastOne(user.getEmail(), getJson(object));
    }

}
