package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.LabelRemoveEvent;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LabelRemoveListener {

    private final ClearCaseVobMapper clearCaseVobMapper;
    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    LabelRemoveListener(ClearCaseVobMapper clearCaseVobMapper,
            ApplicationProperty applicationProperty, ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.clearCaseVobMapper = clearCaseVobMapper;
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handleLabelRemoveEvent(LabelRemoveEvent event) {
        String vobPath = clearCaseVobMapper.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        String tag = event.getTag();

        //remove label
        String removeLabelCommand = ClearCaseCommandBuilder.buildRemoveLabelCommand(viewName, vobPath, tag);
        log.info("Remove label command : {}", removeLabelCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(removeLabelCommand));
    }
}
