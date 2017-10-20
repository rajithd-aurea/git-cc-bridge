package com.redknee.service.event.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.LabelCreateEvent;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class LabelCreateListenerTest {

    @Autowired
    private ClearCaseVobMapper clearCaseVobMapper;

    @Mock
    private ClearCaseCommandExecutor clearCaseCommandExecutor;

    @Autowired
    private ApplicationProperty applicationProperty;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @Test
    public void testHandleLabelCreateEvent() throws Exception {
        doReturn(0).when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        LabelCreateListener listener = new LabelCreateListener(clearCaseVobMapper, applicationProperty,
                clearCaseCommandExecutor);
        listener.handleLabelCreateEvent(
                new LabelCreateEvent("tag1", "rajithd-aurea/vobs_blr", Collections.singletonList("testadd.txt"),
                        Collections.singletonList("testmodify.txt")));
        verify(clearCaseCommandExecutor, times(2)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(2, allValues.size());
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool mklbtype -nc tag1 \" test",
                allValues.get(0).get(0));
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" /usr/atria/bin/cleartool mklabel tag1 /vobs/blr/testadd.txt /vobs/blr/testmodify.txt \" test",
                allValues.get(1).get(0));
    }
}
