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
import com.redknee.service.event.LabelRemoveEvent;
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
public class LabelRemoveListenerTest {

    @Autowired
    private ClearCaseVobMapper clearCaseVobMapper;

    @Mock
    private ClearCaseCommandExecutor clearCaseCommandExecutor;

    @Autowired
    private ApplicationProperty applicationProperty;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @Test
    public void testHandleLabelRemoveEvent() throws Exception {
        doReturn(0).when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        LabelRemoveListener labelRemoveListener = new LabelRemoveListener(clearCaseVobMapper, applicationProperty,
                clearCaseCommandExecutor);
        labelRemoveListener.handleLabelRemoveEvent(new LabelRemoveEvent("tag1", "rajithd-aurea/vobs_blr"));
        verify(clearCaseCommandExecutor, times(1)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(1, allValues.size());
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool rmtype -rmall -force lbtype:tag1 \" test",
                allValues.get(0).get(0));
    }
}
