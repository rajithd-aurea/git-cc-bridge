package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.service.event.AddElementEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddElementListener {

    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    public AddElementListener(ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handle(AddElementEvent event) {
        String vobPath = applicationProperty.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        List<String> newFiles = event.getNewFiles();
        newFiles.stream().forEach(file -> {
            String dir = buildFilePath(file, vobPath);
            String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(viewName, dir, ".");
            log.info("Executing checkout command {}", checkOutCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkOutCommand));

            //make element
            String[] fileParts = file.split("/");
            String makeElementCommand = ClearCaseCommandBuilder
                    .buildMakeElementCommand(viewName, dir, event.getCommitMessage(), fileParts[fileParts.length - 1]);
            log.info("Executing make element command {}", makeElementCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(makeElementCommand));

            //copy file to remote temp location
            String localFile = buildDirPath(event) + "/" + file;
            String remoteFile =
                    applicationProperty.getClearCase().getServer().getWorkspace() + fileParts[fileParts.length - 1];
            log.info("Trying to copy local file {} to remote {}", localFile, remoteFile);
            clearCaseCommandExecutor.copyFile(localFile, remoteFile);

            //copy file from temp to view
            String destinationFile = vobPath + file;
            String copyCommand = ClearCaseCommandBuilder.buildCopyCommand(viewName, remoteFile, destinationFile);
            log.info("Executing copy command {}", copyCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(copyCommand));

            //checkin file
            String checkInCommand = ClearCaseCommandBuilder
                    .buildCheckInCommand(viewName, dir, fileParts[fileParts.length - 1], event.getCommitMessage());
            log.info("Executing checkin command {}", checkInCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInCommand));

            //checkin dir
            String checkInDirCommand = ClearCaseCommandBuilder
                    .buildCheckInCommand(viewName, dir, ".", event.getCommitMessage());
            log.info("Executing directory checkin command {}", checkInDirCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInDirCommand));
        });
    }

    private String buildFilePath(String file, String vob) {
        String[] fileParts = file.split("/");
        if (fileParts.length > 1) {
            String[] dirs = Arrays.copyOf(fileParts, fileParts.length - 1);
            return vob + String.join("/", dirs);
        } else {
            //root level
            return vob;
        }
    }

    private String buildDirPath(AddElementEvent event) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getRepoId();
    }

}
