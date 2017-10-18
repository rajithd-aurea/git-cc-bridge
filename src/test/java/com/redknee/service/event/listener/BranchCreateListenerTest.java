package com.redknee.service.event.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.redknee.Application;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.BranchCreateEvent;
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
public class BranchCreateListenerTest {

    @Autowired
    private ClearCaseVobMapper clearCaseVobMapper;

    @Mock
    private ClearCaseCommandExecutor clearCaseCommandExecutor;

    @Autowired
    private ApplicationProperty applicationProperty;

    @Captor
    private ArgumentCaptor<List<String>> commandArgumentCaptor;

    @Test
    public void testHandleBranchCreateEvent() throws Exception {
        doNothing().when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        BranchCreateListener listener = new BranchCreateListener(clearCaseVobMapper, applicationProperty,
                clearCaseCommandExecutor);
        listener.handleBranchCreateEvent(
                new BranchCreateEvent(true, "branch", "rajithd-aurea/vobs_blr", Collections.singletonList("add.txt"),
                        Collections.singletonList("modify.txt")));
        verify(clearCaseCommandExecutor, times(2)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(2, allValues.size());
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool mkbrtype -nc branch \" test",
                allValues.get(0).get(0));
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool mkbranch -nc -nco branch /vobs/blr/add.txt /vobs/blr/modify.txt \" test",
                allValues.get(1).get(0));
    }

    @Test
    public void testHandleBranchCreateEventWithExistingBranch() throws Exception {
        doNothing().when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        BranchCreateListener listener = new BranchCreateListener(clearCaseVobMapper, applicationProperty,
                clearCaseCommandExecutor);
        listener.handleBranchCreateEvent(
                new BranchCreateEvent(false, "branch", "rajithd-aurea/vobs_blr", Collections.singletonList("add.txt"),
                        Collections.singletonList("modify.txt")));
        verify(clearCaseCommandExecutor).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(1, allValues.size());
        assertEquals(
                "/usr/atria/bin/cleartool setview -exec \" cd /vobs/blr/ && /usr/atria/bin/cleartool mkbranch -nc -nco branch /vobs/blr/add.txt /vobs/blr/modify.txt \" test",
                allValues.get(0).get(0));
    }
}
