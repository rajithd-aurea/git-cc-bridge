package com.redknee.rest.controller;

import com.redknee.rest.dto.EventDto;
import com.redknee.service.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EventController {

    private final EventHandler eventHandler;

    public EventController(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @PostMapping(path = "/bridge/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receiveEvent(@RequestBody EventDto event) {
        log.info("========== Receive event ===================");
        log.info("Body ==> {}", event);
        eventHandler.handleMessage(event);
    }


}
