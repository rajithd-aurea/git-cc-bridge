package com.redknee.rest.controller;

import com.redknee.rest.dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EventController {

    @PostMapping(path = "/bridge/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receiveEvent(@RequestBody EventDto event) {
        log.info("========== Receive event ===================");
        log.info("Body ==> {}", event);

    }


}
