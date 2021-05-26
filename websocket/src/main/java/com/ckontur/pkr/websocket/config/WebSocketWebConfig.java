package com.ckontur.pkr.websocket.config;

import com.ckontur.pkr.common.config.WebConfig;
import com.ckontur.pkr.websocket.component.WebSocketHandler;
import com.ckontur.pkr.websocket.component.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebMvc
@EnableWebSocket
@EnableDiscoveryClient
@Configuration
@ComponentScan(basePackages = "com.ckontur.pkr.common.*")
public class WebSocketWebConfig extends WebConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(webSocketHandler, webSocketHandshakeInterceptor.getEndpoint() + "*")
            .setAllowedOrigins("*")
            .addInterceptors(webSocketHandshakeInterceptor);
    }

    @Override
    public String description() {
        return "Сервис для отправки websocket сообщений.";
    }
}
