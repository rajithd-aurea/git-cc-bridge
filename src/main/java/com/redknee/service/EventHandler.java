package com.redknee.service;

import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
import com.redknee.rest.dto.EventDto.Repository;
import com.redknee.service.event.AddElementEvent;
import com.redknee.service.event.ModifyElementEvent;
import com.redknee.service.event.RemoveElementEvent;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.event.ValidationEvent;
import com.redknee.util.Constants;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class EventHandler {

    private final ApplicationEventPublisher publisher;

    public EventHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Async("eventTaskExecutor")
    public void handleMessage(EventDto event) {
        String ref = event.getRef();
        if (!Constants.MASTER_BRANCH.equals(ref)) {
            log.info("Event is not occurred from master branch change. Ignoring");
            return;
        }
        Repository repository = event.getRepository();
        log.info("Event occurred for master branch for repo {}", repository.getUrl());
        publisher.publishEvent(new ValidationEvent(event));
        publisher.publishEvent(new SourceCodeEvent(repository.getUrl(), repository.getName(), repository.getId()));
        List<Commit> commits = event.getCommits();
        for (Commit commit : commits) {
            List<String> modified = commit.getModified();
            if (!CollectionUtils.isEmpty(modified)) {
                publisher.publishEvent(
                        new ModifyElementEvent(repository.getFullName(), repository.getName(), repository.getId(),
                                attachCommitIdToMessage(commit), modified));
            }

            if (!CollectionUtils.isEmpty(commit.getAdded())) {
                publisher.publishEvent(
                        new AddElementEvent(repository.getFullName(), repository.getName(), repository.getId(),
                                attachCommitIdToMessage(commit), commit.getAdded()));
            }

            if (!CollectionUtils.isEmpty(commit.getRemoved())) {
                publisher.publishEvent(
                        new RemoveElementEvent(repository.getFullName(), repository.getName(), repository.getId(),
                                attachCommitIdToMessage(commit), commit.getRemoved()));
            }
        }

    }

    private String attachCommitIdToMessage(Commit commit) {
        return commit.getMessage() + " - " + commit.getId();
    }


}
