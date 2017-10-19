package com.redknee.service.event;

import java.util.List;

public class ModifyElementEvent extends ElementEvent {

    public ModifyElementEvent(String repoFullName, String repoName, String repoId, String deliveryId,
            String commitMessage, List<String> files) {
        super(repoFullName, repoName, repoId, deliveryId, commitMessage, files);
    }
}
