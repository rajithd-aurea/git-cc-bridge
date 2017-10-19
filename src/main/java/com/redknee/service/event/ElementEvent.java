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
public class ElementEvent {

    private String repoFullName;
    private String repoName;
    private String repoId;
    private String deliveryId;
    private String commitMessage;
    private List<String> files;

}
