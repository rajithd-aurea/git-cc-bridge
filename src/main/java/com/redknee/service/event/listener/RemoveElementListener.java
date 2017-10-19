package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.RemoveElementEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RemoveElementListener extends AbstractListener {

    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    RemoveElementListener(ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor, ClearCaseVobMapper clearCaseVobMapper) {
        super(applicationProperty, clearCaseVobMapper, clearCaseCommandExecutor);
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handle(RemoveElementEvent event) {
        List<String> removedFiles = event.getRemovedFiles();
        log.info("Trying to remove {} number of files", removedFiles);
        String vobPath = getVobPath(event.getRepoFullName());
        removedFiles.stream().forEach(file -> {
            String directory = getDirectory(file);
            String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(getViewName(), vobPath, directory);
            log.info("Checkout directory command {}", checkOutCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkOutCommand));

            String removeCommand = ClearCaseCommandBuilder
                    .buildRemoveElementCommand(getViewName(), vobPath, event.getCommitMessage(), file);
            log.info("Remove file command {}", removeCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(removeCommand));

            String checkInCommand = ClearCaseCommandBuilder
                    .buildCheckInCommand(getViewName(), vobPath, directory, event.getCommitMessage());
            log.info("Checkin directory command {}", checkInCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInCommand));
        });
    }

    private String getDirectory(String file) {
        String[] fileParts = file.split("/");
        if (fileParts.length == 1) {
            return ".";
        } else {
            String[] dirs = Arrays.copyOf(fileParts, fileParts.length - 1);
            return StringUtils.join("/", dirs);
        }
    }
}
