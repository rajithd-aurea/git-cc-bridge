package com.redknee.service.event;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCreateEvent extends ElementEvent {

    private boolean isNew;
    private String branch;

    public BranchCreateEvent(boolean isNew, String branch, String repoFullName, String repoName, String repoId,
            String deliveryId, String commitMessage, List<String> files) {
        super(repoFullName, repoName, repoId, deliveryId, commitMessage, files);
        this.isNew = isNew;
        this.branch = branch;
    }
}
