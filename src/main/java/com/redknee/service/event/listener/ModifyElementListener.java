package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.service.event.ModifyElementEvent;
import java.util.Collections;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ModifyElementListener {

    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    ModifyElementListener(ApplicationProperty applicationProperty, ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handle(ModifyElementEvent element) {
        String vobPath = applicationProperty.getPathMapper().get(element.getRepoFullName());
        String viewName = applicationProperty.getClearCase().getViewName();
        List<String> modifiedFiles = element.getModifiedFiles();
        modifiedFiles.stream().forEach(file -> {
            // checkout command
            String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(viewName, vobPath, file);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkOutCommand));

            // copy file to /tmp
            String localFile = buildDirPath(element) + "/" + file;
            String[] fileParts = file.split("/");
            String remoteFile =
                    applicationProperty.getClearCase().getServer().getWorkspace() + fileParts[fileParts.length - 1];
            clearCaseCommandExecutor.copyFile(localFile, remoteFile);

            // copy file from /tmp to view
            String destinationFile = vobPath + file;
            String copyCommand = ClearCaseCommandBuilder.buildCopyCommand(viewName, remoteFile, destinationFile);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(copyCommand));

            // checkin command
            String checkInCommand = ClearCaseCommandBuilder
                    .buildCheckInCommand(viewName, vobPath, file, element.getCommitMessage());
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInCommand));

        });
    }

    private String buildDirPath(ModifyElementEvent event) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getRepoId();
    }
}
