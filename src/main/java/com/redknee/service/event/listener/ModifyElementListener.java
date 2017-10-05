package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.external.LinuxCommandBuilder;
import com.redknee.service.event.ModifyElementEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
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
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        List<String> modifiedFiles = element.getModifiedFiles();
        modifiedFiles.stream().forEach(file -> {
            // checkout command
            String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(viewName, vobPath, file);
            log.info("Executing checkout command {}", checkOutCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkOutCommand));

            //create folder structure in /tmp/
            String remoteDirPath = buildRemoteFilePath(element, file);
            String dirCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteDirPath);
            log.info("Create directory command in remote location {}", dirCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(dirCommand));

            // copy file to /tmp/<repo_name>
            String localFile = buildGitDirPath(element) + "/" + file;
            String[] fileParts = file.split("/");
            String remoteFile = remoteDirPath + "/" + fileParts[fileParts.length - 1];
            log.info("Trying to copy local file {} to remote {}", localFile, remoteFile);
            clearCaseCommandExecutor.copyFile(localFile, remoteFile);

            // copy file from /tmp to view
            String destinationFile = vobPath + file;
            String copyCommand = ClearCaseCommandBuilder.buildCopyCommand(viewName, remoteFile, destinationFile);
            log.info("Executing copy command {}", copyCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(copyCommand));

            // checkin command
            String checkInCommand = ClearCaseCommandBuilder
                    .buildCheckInCommand(viewName, vobPath, file, element.getCommitMessage());
            log.info("Executing checkin command {}", checkInCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInCommand));
        });
    }

    private String buildRemoteFilePath(ModifyElementEvent element, String file) {
        String remoteDirPath = buildRemoteDirPath(element);
        String[] fileParts = file.split("/");
        if (fileParts.length > 1) {
            String[] dirs = Arrays.copyOf(fileParts, fileParts.length - 1);
            return remoteDirPath + "/" + String.join("/", dirs);
        } else {
            return remoteDirPath;
        }
    }

    private String buildGitDirPath(ModifyElementEvent event) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getRepoId();
    }

    private String buildRemoteDirPath(ModifyElementEvent event) {
        String workspace = applicationProperty.getClearCase().getServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getRepoId();
    }
}
