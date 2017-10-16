package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.LabelCreateEvent;
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
public class LabelCreateListener {

    private final ClearCaseVobMapper clearCaseVobMapper;
    private final ApplicationProperty applicationProperty;
    private final ClearCaseCommandExecutor clearCaseCommandExecutor;

    LabelCreateListener(ClearCaseVobMapper clearCaseVobMapper, ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor) {
        this.clearCaseVobMapper = clearCaseVobMapper;
        this.applicationProperty = applicationProperty;
        this.clearCaseCommandExecutor = clearCaseCommandExecutor;
    }

    @EventListener
    public void handleLabelCreateEvent(LabelCreateEvent event) {
        String vobPath = clearCaseVobMapper.getPathMapper().get(event.getRepoFullName());
        log.info("Found VOB path {}", vobPath);
        String viewName = applicationProperty.getClearCase().getViewName();
        String tag = event.getTag();

        // create label command
        String command = ClearCaseCommandBuilder.buildCreateLabelCommand(viewName, vobPath, tag);
        log.info("Create label command : {}", command);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(command));

        if (CollectionUtils.isEmpty(event.getAddedFiles()) && CollectionUtils.isEmpty(event.getModifiedFiles())) {
            return;
        }

        List<String> files = new ArrayList<>();
        files.addAll(event.getAddedFiles());
        files.addAll(event.getModifiedFiles());
        List<String> filesWithPath = files.stream().map(file -> vobPath + file).collect(Collectors.toList());
        log.info("Trying to attach label {} to {} files", tag, filesWithPath.size());

        //attach label to files
        String attachLabelCommand = ClearCaseCommandBuilder
                .buildAttachLabelCommand(viewName, tag, filesWithPath.toArray(new String[filesWithPath.size()]));
        log.info("Attach label to files", attachLabelCommand);
        clearCaseCommandExecutor.executeCommand(Collections.singletonList(attachLabelCommand));
    }
}
