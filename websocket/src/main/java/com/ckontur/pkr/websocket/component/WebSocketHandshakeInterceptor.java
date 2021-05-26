package com.ckontur.pkr.websocket.component;

import com.ckontur.pkr.common.exception.InvalidArgumentException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Getter
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${web.websocket-endpoint:/streams/}")
    private String endpoint;

    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) throws Exception {
        String[] channelAndId = request.getURI().getPath().substring(endpoint.length()).split("/");
        if (channelAndId.length != 2) {
            throw new InvalidArgumentException("Неверно указаны канал и адрес потока.");
        }
        attributes.put("channel", channelAndId[0]);
        attributes.put("id", channelAndId[1]);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do here
    }
}
