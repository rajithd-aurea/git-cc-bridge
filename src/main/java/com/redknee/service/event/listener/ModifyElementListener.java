package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandBuilder;
import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
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
        EventDto event = element.getEvent();
        String vobPath = applicationProperty.getPathMapper().get(event.getRepository().getFullName());
        String viewName = applicationProperty.getClearCase().getViewName();
        List<Commit> commits = event.getCommits();
        for (Commit commit : commits) {
            List<String> modifiedElements = commit.getModified();
            modifiedElements.stream().forEach(ele -> {
                // checkout command
                String checkOutCommand = ClearCaseCommandBuilder.buildCheckOutCommand(viewName, vobPath, ele);
                clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkOutCommand));

                // copy file to /tmp
                String localFile = buildDirPath(event) + "/" + ele;
                String[] fileParts = ele.split("/");
                String remoteFile =
                        applicationProperty.getClearCase().getServer().getWorkspace() + fileParts[fileParts.length - 1];
                clearCaseCommandExecutor.copyFile(localFile, remoteFile);

                // copy file from /tmp to view
                String destinationFile = vobPath + ele;
                String copyCommand = ClearCaseCommandBuilder.buildCopyCommand(viewName, remoteFile, destinationFile);
                clearCaseCommandExecutor.executeCommand(Collections.singletonList(copyCommand));

                // checkin command
                String checkInCommand = ClearCaseCommandBuilder
                        .buildCheckInCommand(viewName, vobPath, ele, commit.getMessage());
                clearCaseCommandExecutor.executeCommand(Collections.singletonList(checkInCommand));

            });
        }
    }

    private String buildDirPath(EventDto event) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + event.getRepository().getName() + "-" + event.getRepository().getId();
    }
}
