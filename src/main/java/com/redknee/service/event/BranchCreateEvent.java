package com.redknee.service.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchCreateEvent {

    private boolean isNew;
    private String branch;
    private String repoFullName;
    private List<String> addedFiles;
    private List<String> modifiedFiles;

}
