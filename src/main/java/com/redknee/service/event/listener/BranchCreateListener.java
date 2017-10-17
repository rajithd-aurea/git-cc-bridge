package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.BranchCreateEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class BranchCreateListener {

    private final ClearCaseVobMapper clearCaseVobMapper;
    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    BranchCreateListener(ClearCaseVobMapper clearCaseVobMapper,
            ApplicationProperty applicationProperty, ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.clearCaseVobMapper = clearCaseVobMapper;
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handleBranchCreateEvent(BranchCreateEvent event) {
        String vobPath = clearCaseVobMapper.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        String branch = event.getBranch();
        if (event.isNew()) {
            // branch is new. Create branch type
            log.info("Branch [{}] is new. Creating branch type", branch);
            String branchTypeCommand = ClearCaseCommandBuilder.buildCreateBranchTypeCommand(viewName, vobPath, branch);
            log.info("Create branch type command : {}", branchTypeCommand);
            clearCaseCommandExecutor.executeCommand(Collections.singletonList(branchTypeCommand));
        }
        if (CollectionUtils.isEmpty(event.getAddedFiles()) && CollectionUtils.isEmpty(event.getModifiedFiles())) {
            return;
        }
        List<String> files = new ArrayList<>();
        files.addAll(event.getAddedFiles());
        files.addAll(event.getModifiedFiles());
        List<String> filesWithPath = files.stream().map(file -> vobPath + file).collect(Collectors.toList());
        log.info("Trying to attach branch {} to {} files", branch, filesWithPath.size());
        String attachBranchCommand = ClearCaseCommandBuilder
                .buildAssignBranchToElementsCommand(viewName, vobPath, branch,
                        filesWithPath.toArray(new String[filesWithPath.size()]));
        log.info("Attach branch to files command {}", attachBranchCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(attachBranchCommand));
    }
}
