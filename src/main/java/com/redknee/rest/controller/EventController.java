package com.redknee.rest.controller;

import com.redknee.config.ClearCaseVobMapper;
import com.redknee.rest.dto.EventDto;
import com.redknee.service.EventHandler;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EventController {

    private final EventHandler eventHandler;
    private final ClearCaseVobMapper clearCaseVobMapper;

    public EventController(EventHandler eventHandler, ClearCaseVobMapper clearCaseVobMapper) {
        this.eventHandler = eventHandler;
        this.clearCaseVobMapper = clearCaseVobMapper;
    }

    @PostMapping(path = "/bridge/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receiveEvent(@RequestBody EventDto event) {
        Map<String, String> pathMapper = clearCaseVobMapper.getPathMapper();
        log.info("========== Receive event ===================");
        eventHandler.handleMessage(event);
    }
}
