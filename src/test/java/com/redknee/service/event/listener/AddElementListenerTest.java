package com.redknee.service.event.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.AddElementEvent;
import java.util.Arrays;
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
public class AddElementListenerTest {

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
        doNothing().when(clearCaseCommandExecutor).copyFile(Mockito.anyString(), Mockito.anyString());

        AddElementListener listener = new AddElementListener(applicationProperty,
                clearCaseCommandExecutor, clearCaseVobMapper);
        listener.handle(new AddElementEvent("rajithd-aurea/vobs_blr", "vobs_blr", "123", "1", "commit",
                Collections.singletonList("test.txt")));

        verify(clearCaseCommandExecutor, times(3)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(3, allValues.size());
        assertEquals("mkdir -p vobs_blr-1", allValues.get(0).get(0));
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/clearfsimport -comment 'commit' -rec -nset vobs_blr-1/* . \" test",
                allValues.get(1).get(0));
    }

    @Test
    public void testHandleMultipleFiles() throws Exception {
        doNothing().when(clearCaseCommandExecutor).executeCommand(Mockito.any());
        doNothing().when(clearCaseCommandExecutor).copyFile(Mockito.anyString(), Mockito.anyString());

        AddElementListener listener = new AddElementListener(applicationProperty,
                clearCaseCommandExecutor, clearCaseVobMapper);
        listener.handle(new AddElementEvent("rajithd-aurea/vobs_blr", "vobs_blr", "123", "1", "commit",
                Arrays.asList("text.txt", "test/abc.txt")));

        verify(clearCaseCommandExecutor, times(4)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(4, allValues.size());
    }
}
