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
import java.util.Arrays;
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
                new BranchCreateEvent(true, "branch", "rajithd-aurea/vobs_blr", "rajithd-aurea", "1", "1", "c",
                        Arrays.asList("add.txt", "modify.txt")));
        verify(clearCaseCommandExecutor, times(5)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(5, allValues.size());
    }

    @Test
    public void testHandleBranchCreateEventWithExistingBranch() throws Exception {
        doNothing().when(clearCaseCommandExecutor).executeCommand(Mockito.any());

        BranchCreateListener listener = new BranchCreateListener(clearCaseVobMapper, applicationProperty,
                clearCaseCommandExecutor);
        listener.handleBranchCreateEvent(
                new BranchCreateEvent(false, "branch", "rajithd-aurea/vobs_blr", "rajithd-aurea", "1", "1", "c",
                        Arrays.asList("add.txt", "modify.txt")));
        verify(clearCaseCommandExecutor, times(4)).executeCommand(commandArgumentCaptor.capture());
        List<List<String>> allValues = commandArgumentCaptor.getAllValues();
        assertEquals(4, allValues.size());
    }
}
