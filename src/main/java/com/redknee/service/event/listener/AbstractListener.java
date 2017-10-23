package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.external.LinuxCommandBuilder;
import com.redknee.service.event.ElementEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractListener {

    private final ApplicationProperty applicationProperty;
    private final ClearCaseVobMapper clearCaseVobMapper;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    AbstractListener(ApplicationProperty applicationProperty, ClearCaseVobMapper clearCaseVobMapper,
            ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.applicationProperty = applicationProperty;
        this.clearCaseVobMapper = clearCaseVobMapper;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    protected void handleAddAndModifyEvent(ElementEvent event) {
        //create remote directory in CC server
        String remoteDirPath = buildRemoteDirPath(event.getRepoName(), event.getDeliveryId());
        String dirCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteDirPath);
        log.info("Create directory command in remote location {}", dirCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(dirCommand));

        List<String> files = event.getFiles();
        files.stream().forEach(file -> {
            String remoteFilePath = buildRemoteFilePath(event.getRepoName(), event.getDeliveryId(), file);
            if (!remoteDirPath.equals(remoteFilePath)) {
                String remoteFilePathCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteFilePath);
                log.info("Create remote directory {} for file {}", remoteFilePathCommand, file);
                clearCaseCommandExecutor.executeCommand(Collections.singletonList(remoteFilePathCommand));
            }

            // copy local file to CC server. /tmp/<repo>
            String localFile = buildGitDirPath(event.getRepoName(), event.getRepoId()) + "/" + file;
            String[] fileParts = file.split("/");
            String remoteFile = remoteFilePath + "/" + fileParts[fileParts.length - 1];
            log.info("Trying to copy local file {} to remote {}", localFile, remoteFile);
            clearCaseCommandExecutor.copyFile(localFile, remoteFile);
        });

        String clearFsCommand = ClearCaseCommandBuilder
                .buildCreateNewFilesAndDirsCommand(getViewName(), getVobPath(event.getRepoFullName()),
                        event.getCommitMessage(), remoteDirPath + "/*");
        log.info("Execute CC command {}", clearFsCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(clearFsCommand));

        //cleanup temp dir
        cleanupTempDir(remoteDirPath);
    }

    protected void cleanupTempDir(String remoteDirPath) {
        String cleanUpCommand = LinuxCommandBuilder.buildRemoveDirCommand(remoteDirPath);
        log.info("Cleaning directory {}", cleanUpCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(cleanUpCommand));
    }

    protected String buildGitDirPath(String repoName, String repoId) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + repoName + "-" + repoId;
    }

    protected String buildRemoteDirPath(String repoName, String deliveryId) {
        String workspace = applicationProperty.getClearCase().getServer().getWorkspace();
        return workspace + repoName + "-" + deliveryId;
    }

    protected String buildRemoteFilePath(String repoName, String deliveryId, String file) {
        String remoteDirPath = buildRemoteDirPath(repoName, deliveryId);
        String[] fileParts = file.split("/");
        if (fileParts.length > 1) {
            String[] dirs = Arrays.copyOf(fileParts, fileParts.length - 1);
            return remoteDirPath + "/" + String.join("/", dirs);
        } else {
            return remoteDirPath;
        }
    }

    protected String getVobPath(String repoFullName) {
        return clearCaseVobMapper.getPathMapper().get(repoFullName);
    }

    protected String getViewName() {
        return applicationProperty.getClearCase().getViewName();
    }

}
