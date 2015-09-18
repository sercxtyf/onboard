package com.onboard.frontend.websocket;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.RequestContextFilter;

import com.onboard.frontend.model.Activity;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.web.RequestFilter;

@Service
public class RedisMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageReceiver.class);

    @Autowired
    private WebsocketHandler websocketHandler;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("channel"));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public RequestContextFilter registrationRequestFilter() {
        RequestContextFilter requestContextFilter = new RequestFilter();
        return requestContextFilter;
    }

    public void receiveMessage(String message) {
        LOGGER.info("Received <" + message + ">");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {

            Activity activity = mapper.readValue(message, Activity.class);
            ArrayList<User> users = (ArrayList<User>) activity.getSubscribers();
            if (users != null) {
                for (User user : users) {
                    websocketHandler.sendMessage(user.getEmail(), message);
                }
            }
        } catch (IOException e) {
            LOGGER.info("message is not type of activity");
        }
    }
}
