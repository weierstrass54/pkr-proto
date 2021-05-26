package com.ckontur.pkr.websocket.controller;

import com.ckontur.pkr.common.component.web.WebSocketMessage;
import com.ckontur.pkr.websocket.component.WebSocketHandler;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Вебсокет-сообщения"})
@RestController
@RequestMapping("/websocket")
@PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNAL')")
@RequiredArgsConstructor
@Timed(value = "requests.websocket", percentiles = {0.75, 0.9, 0.95, 0.99})
public class WebSocketController {
    private final WebSocketHandler webSocketHandler;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody WebSocketMessage<?, ?> message) {
        return webSocketHandler.sendMessage(message) ?
            new ResponseEntity<>(HttpStatus.ACCEPTED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
