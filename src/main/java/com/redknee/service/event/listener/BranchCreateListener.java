package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.external.LinuxCommandBuilder;
import com.redknee.service.event.BranchCreateEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class BranchCreateListener extends AbstractListener {

    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    BranchCreateListener(ClearCaseVobMapper clearCaseVobMapper,
            ApplicationProperty applicationProperty, ClearCaseCommandExecutor clearCaseCommandExecutor) {
        super(applicationProperty, clearCaseVobMapper, clearCaseCommandExecutor);
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handleBranchCreateEvent(BranchCreateEvent event) {
        String vobPath = getVobPath(event.getRepoFullName());
        String viewName = getViewName();
        String branch = event.getBranch();
        String remoteDirPath = buildRemoteDirPath(event.getRepoName(), event.getDeliveryId());
        if (event.isNew()) {
            // branch is new. Create branch type
            log.info("Branch [{}] is new. Creating branch type", branch);
            String branchTypeCommand = ClearCaseCommandBuilder.buildCreateBranchTypeCommand(viewName, vobPath, branch);
            log.info("Create branch type command : {}", branchTypeCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(branchTypeCommand));
        }

        if (!CollectionUtils.isEmpty(event.getFiles())) {
            // this will only include the addedFiles
            handleAddAndModifyEvent(event);
            log.info("Trying to attach branch {} to {} files", branch, event.getFiles().size());
            event.getFiles().forEach(file -> {
                //attach branch and checkout
                String attachBranchCommand = ClearCaseCommandBuilder
                        .buildAssignBranchToElementsWithCoCommand(viewName, vobPath, branch, file);
                //checkin file
                String checkInCommand = ClearCaseCommandBuilder
                        .buildCheckInCommand(viewName, vobPath, file, event.getCommitMessage());
                clearCaseCommandExecutor.executeCommand(Arrays.asList(attachBranchCommand, checkInCommand));
            });
        }

        if (!CollectionUtils.isEmpty(event.getModifiedFiles())) {
            // handle modified files
            List<String> modifiedFiles = event.getModifiedFiles();
            log.info("Found {} number of modified files to attach new branch {}", modifiedFiles.size(), branch);
            String dirCommand = LinuxCommandBuilder.buildCreateDirCommand(remoteDirPath);
            log.info("Create directory command in remote location {}", dirCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(dirCommand));
            modifiedFiles.forEach(file -> {
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

                // attach branch and checkout
                String branchCommand = ClearCaseCommandBuilder
                        .buildAssignBranchToElementsCommand(viewName, vobPath, branch, file);
                log.info("Attach branch command {}", branchCommand);

                // checkout file
                String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(viewName, vobPath, file);
                log.info("checkout command {}", checkOutCommand);

                // copy file
                String copyCommand = ClearCaseCommandBuilder.buildCopyCommand(viewName, remoteFile, vobPath + file);
                log.info("Copy file command {}", copyCommand);

                // checkin file
                String checkInCommand = ClearCaseCommandBuilder
                        .buildCheckInCommand(viewName, vobPath, file, event.getCommitMessage());
                log.info("Checkin file for branch {}", checkInCommand);
                clearCaseCommandExecutor
                        .executeCommand(Arrays.asList(branchCommand, checkOutCommand, copyCommand, checkInCommand));
            });
        }
        // cleanup dir
        cleanupTempDir(remoteDirPath);
    }
}
