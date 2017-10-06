package com.redknee.service.event.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.ModifyElementEvent;
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
public class ModifyElementListenerTest {

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

        ModifyElementListener modifyElementListener = new ModifyElementListener(applicationProperty,
                clearCaseCommandExecutor, clearCaseVobMapper);
        modifyElementListener.handle(new ModifyElementEvent("rajithd-aurea/vobs_blr", "vobs_blr", "123", "commit",
                Collections.singletonList("test.txt")));

        verify(clearCaseCommandExecutor, times(4)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(4, allValues.size());
        String checkoutCommand = allValues.get(0).get(0);
        assertEquals(checkoutCommand,
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool checkout -reserved -nc test.txt \" test");
        String dirCommand = allValues.get(1).get(0);
        assertEquals(dirCommand, "mkdir -p vobs_blr-123");
        String copyCommand = allValues.get(2).get(0);
        assertEquals(copyCommand,
                "/usr/atria/bin/cleartool setview -exec \" cp vobs_blr-123/test.txt /vobs/blr/test.txt \" test");
        String checkInCommand = allValues.get(3).get(0);
        assertEquals(checkInCommand,
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool ci -c 'commit' -ide test.txt \" test");
    }
}
