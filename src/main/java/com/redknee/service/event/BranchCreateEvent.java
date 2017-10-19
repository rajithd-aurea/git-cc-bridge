package com.redknee.service.event;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCreateEvent extends ElementEvent {

    private boolean isNew;
    private String branch;
    private List<String> addedFiles;
    private List<String> modifiedFiles;

    public BranchCreateEvent(boolean isNew, String branch, String repoFullName, String repoName, String repoId,
            String deliveryId, String commitMessage, List<String> addedFiles, List<String> modifiedFiles) {
        super(repoFullName, repoName, repoId, deliveryId, commitMessage, addedFiles);
        this.isNew = isNew;
        this.branch = branch;
        this.addedFiles = addedFiles;
        this.modifiedFiles = modifiedFiles;
    }
}
