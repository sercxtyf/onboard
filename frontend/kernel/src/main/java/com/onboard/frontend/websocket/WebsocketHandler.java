package com.onboard.frontend.websocket;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.web.SessionService;

/**
 * Echo messages by implementing a Spring {@link WebSocketHandler} abstraction.
 */
@Service
public class WebsocketHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private final Multimap<String, WebSocketSession> userPagesMap;

    @Autowired
    private SessionService sessionService;

    public WebsocketHandler() {
        userPagesMap = ArrayListMultimap.create();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        logger.info("Opened new session in instance " + this);
        User user = sessionService.getCurrentUser();
        if (user != null) {
            userPagesMap.put(user.getEmail(), session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info(message.toString());
        // session.sendMessage(new TextMessage(echoMessage));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

    public void sendMessage(String userEmail, String message) {

        Multimap<String, WebSocketSession> syncMap = Multimaps.synchronizedMultimap(userPagesMap);
        Collection<WebSocketSession> mis = syncMap.get(userEmail);
        synchronized (syncMap) {
            if (mis != null) {
                Iterator<WebSocketSession> it = mis.iterator();
                while (it.hasNext()) {
                    WebSocketSession session = it.next();
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        logger.info("The WebSocket connection has been closed: " + session.toString());
                    }

                }
            }
        }

    }

}
