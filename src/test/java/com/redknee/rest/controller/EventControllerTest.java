package com.redknee.rest.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.redknee.Application;
import com.redknee.rest.dto.EventDto;
import com.redknee.service.EventHandler;
import java.io.File;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class EventControllerTest {

    private static final String EVENT_MESSAGE = "./src/test/resources/messages/event_1.json";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private EventHandler eventHandler;

    @Autowired
    private EventController eventController;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        doNothing().when(eventHandler).handleMessage(Mockito.any(EventDto.class));

        ReflectionTestUtils.setField(eventController, "eventHandler", eventHandler);
    }

    @Test
    public void testReceiveEvent() throws Exception {
        mockMvc.perform(post("/bridge/event").content(FileUtils.readFileToString(new File(EVENT_MESSAGE),
                Charset.defaultCharset())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
