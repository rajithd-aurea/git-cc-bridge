package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.service.event.RemoveElementEvent;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RemoveElementListener {

    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    RemoveElementListener(ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handle(RemoveElementEvent event) {
        String vobPath = applicationProperty.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        List<String> removedFiles = event.getRemovedFiles();
        log.info("Trying to remove {} number of files", removedFiles);
        removedFiles.stream().forEach(file -> {
            String filePath = vobPath + file;
            String removeElementCommand = ClearCaseCommandBuilder
                    .buildRemoveElementCommand(viewName, event.getCommitMessage(), filePath);
            log.info("Executing remove element command {}", removeElementCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(removeElementCommand));
        });
    }
}
