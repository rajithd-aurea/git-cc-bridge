package com.redknee.service.event.listener;

import static org.junit.Assert.assertTrue;

import com.redknee.Application;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Repository;
import com.redknee.service.event.ValidationEvent;
import com.redknee.service.exception.ApiException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ValidationListenerTest {

    private ClearCaseVobMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ClearCaseVobMapper();
        Map<String, String> map = new HashMap<>();
        map.put("repo_git", "repo_vob");
        mapper.setPathMapper(map);
    }

    @Test
    public void testHandleVobExists() throws Exception {
        EventDto eventDto = new EventDto();
        Repository repository = new Repository();
        repository.setFullName("repo_git");
        eventDto.setRepository(repository);
        ValidationListener listener = new ValidationListener(mapper);
        listener.handle(new ValidationEvent(eventDto));
        assertTrue(true);
    }

    @Test(expected = ApiException.class)
    public void testHandleVobNotExists() throws Exception {
        EventDto eventDto = new EventDto();
        Repository repository = new Repository();
        repository.setFullName("repo_git1");
        eventDto.setRepository(repository);
        ValidationListener listener = new ValidationListener(mapper);
        listener.handle(new ValidationEvent(eventDto));
    }
}
