package com.redknee;

import com.redknee.rest.dto.EventDto;
import com.redknee.service.EventHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        EventHandler bean = run.getBean(EventHandler.class);
        bean.handleMessage(new EventDto());
    }
}
