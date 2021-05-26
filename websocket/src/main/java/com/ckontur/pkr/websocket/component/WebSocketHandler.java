package com.ckontur.pkr.websocket.component;

import com.ckontur.pkr.common.component.web.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String key = keyOf(session);
        if (!sessions.containsKey(key)) {
            sessions.put(key, new CopyOnWriteArrayList<>());
        }
        sessions.get(key).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String key = keyOf(session);
        if (sessions.containsKey(key)) {
            sessions.get(key).remove(session);
            if (sessions.get(key).isEmpty()) {
                sessions.remove(key);
            }
        }
    }

    public boolean sendMessage(WebSocketMessage<? ,?> message) {
        return sessions.getOrDefault(keyOf(message), Collections.emptyList())
            .stream().map(session -> sendMessage(session, message))
            .reduce(true, (a, b) -> a && b);
    }

    private boolean sendMessage(WebSocketSession session, WebSocketMessage<?, ?> message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            return true;
        }
        catch (IOException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
            return false;
        }
    }

    private static String keyOf(WebSocketSession session) {
        return session.getAttributes().get("channel").toString() + "#" + session.getAttributes().get("id").toString();
    }

    private static String keyOf(WebSocketMessage<?, ?> message) {
        return message.getChannel() + "#" + message.getKey().toString();
    }

}
