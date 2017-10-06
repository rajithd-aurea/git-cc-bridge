package com.redknee.service.event.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.RemoveElementEvent;
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
public class RemoveElementListenerTest {

    @Autowired
    private ClearCaseVobMapper clearCaseVobMapper;

    @Mock
    private ClearCaseCommandExecutor clearCaseCommandExecutor;

    @Autowired
    private ApplicationProperty applicationProperty;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @Test
    public void testHandle() throws Exception {
        doNothing().when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        RemoveElementListener removeElementListener = new RemoveElementListener(applicationProperty,
                clearCaseCommandExecutor, clearCaseVobMapper);
        removeElementListener.handle(new RemoveElementEvent("rajithd-aurea/vobs_blr", "vobs_blr", "123", "commit",
                Collections.singletonList("test.txt")));

        verify(clearCaseCommandExecutor, times(1)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(1, allValues.size());
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" /usr/atria/bin/cleartool rmelem -c 'commit' -force /vobs/blr/test.txt \" test",
                allValues.get(0).get(0));
    }
}
