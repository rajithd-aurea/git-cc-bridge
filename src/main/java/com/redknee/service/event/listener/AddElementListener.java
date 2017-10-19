package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.external.LinuxCommandBuilder;
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
    private final ClearCaseVobMapper clearCaseVobMapper;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    public AddElementListener(ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor, ClearCaseVobMapper clearCaseVobMapper) {
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
        this.clearCaseVobMapper = clearCaseVobMapper;
    }

    @EventListener
    public void handle(AddElementEvent event) {
        String vobPath = clearCaseVobMapper.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();

        //create remote directory in CC server
        String remoteDirPath = buildRemoteDirPath(event);
        String dirCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteDirPath);
        log.info("Create directory command in remote location {}", dirCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(dirCommand));

        List<String> newFiles = event.getNewFiles();
        newFiles.stream().forEach(file -> {
            String remoteFilePath = buildRemoteFilePath(event, file);
            if (!remoteDirPath.equals(remoteFilePath)) {
                String remoteFilePathCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteFilePath);
                log.info("Create remote directory for file {}", remoteFilePathCommand);
                clearCaseCommandExecutor.executeCommand(Collections.singletonList(remoteFilePathCommand));
            }

            // copy local file to CC server. /tmp/<repo>
            String localFile = buildGitDirPath(event) + "/" + file;
            String[] fileParts = file.split("/");
            String remoteFile = remoteFilePath + "/" + fileParts[fileParts.length - 1];
            log.info("Trying to copy local file {} to remote {}", localFile, remoteFile);
            clearCaseCommandExecutor.copyFile(localFile, remoteFile);
        });

        String clearFsCommand = ClearCaseCommandBuilder
                .buildCreateNewFilesAndDirsCommand(viewName, vobPath, event.getCommitMessage(), remoteDirPath + "/*");
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(clearFsCommand));
    }

    private String buildGitDirPath(AddElementEvent event) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getRepoId();
    }

    private String buildRemoteDirPath(AddElementEvent event) {
        String workspace = applicationProperty.getClearCase().getServer().getWorkspace();
        return workspace + event.getRepoName() + "-" + event.getDeliveryId();
    }

    private String buildRemoteFilePath(AddElementEvent element, String file) {
        String remoteDirPath = buildRemoteDirPath(element);
        String[] fileParts = file.split("/");
        if (fileParts.length > 1) {
            String[] dirs = Arrays.copyOf(fileParts, fileParts.length - 1);
            return remoteDirPath + "/" + String.join("/", dirs);
        } else {
            return remoteDirPath;
        }
    }

}
