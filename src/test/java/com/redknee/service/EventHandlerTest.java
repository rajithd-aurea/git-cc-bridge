package com.redknee.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
import com.redknee.rest.dto.EventDto.Repository;
import com.redknee.service.event.AddElementEvent;
import com.redknee.service.event.ModifyElementEvent;
import com.redknee.service.event.RemoveElementEvent;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.event.ValidationEvent;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class EventHandlerTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Before
    public void setUp() throws Exception {
        doNothing().when(publisher).publishEvent(any(ValidationEvent.class));
        doNothing().when(publisher).publishEvent(any(SourceCodeEvent.class));
        doNothing().when(publisher).publishEvent(any(ModifyElementEvent.class));
        doNothing().when(publisher).publishEvent(any(AddElementEvent.class));
        doNothing().when(publisher).publishEvent(any(RemoveElementEvent.class));

    }

    @Test
    public void testHandleMessageWithModifiedFiles() throws Exception {
        EventHandler eventHandler = new EventHandler(publisher);
        EventDto eventDto = createEventDto();
        Commit commit = new Commit();
        commit.setModified(Collections.singletonList("test.txt"));
        eventDto.setCommits(Collections.singletonList(commit));
        eventHandler.handleMessage(eventDto);

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(3)).publishEvent(argumentCaptor.capture());
        List<Object> allValues = argumentCaptor.getAllValues();
        assertEquals(3, allValues.size());
    }

    @Test
    public void testHandleMessageWithModifiedAndAddedFiles() throws Exception {
        EventHandler eventHandler = new EventHandler(publisher);
        EventDto eventDto = createEventDto();
        Commit commit = new Commit();
        commit.setModified(Collections.singletonList("test.txt"));
        commit.setAdded(Collections.singletonList("test1.txt"));
        eventDto.setCommits(Collections.singletonList(commit));
        eventHandler.handleMessage(eventDto);

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(4)).publishEvent(argumentCaptor.capture());
        List<Object> allValues = argumentCaptor.getAllValues();
        assertEquals(4, allValues.size());
    }

    @Test
    public void testHandleMessageWithRemovedFiles() throws Exception {
        EventHandler eventHandler = new EventHandler(publisher);
        EventDto eventDto = createEventDto();
        Commit commit = new Commit();
        commit.setRemoved(Collections.singletonList("test.txt"));
        eventDto.setCommits(Collections.singletonList(commit));
        eventHandler.handleMessage(eventDto);

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(3)).publishEvent(argumentCaptor.capture());
        List<Object> allValues = argumentCaptor.getAllValues();
        assertEquals(3, allValues.size());
    }

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setRef("refs/heads/master");
        Repository repository = new Repository();
        repository.setFullName("rajithd-aurea/vobs");
        repository.setName("vobs");
        eventDto.setRepository(repository);
        return eventDto;
    }
}
