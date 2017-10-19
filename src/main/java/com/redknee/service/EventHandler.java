package com.redknee.service;

import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
import com.redknee.rest.dto.EventDto.Repository;
import com.redknee.service.event.AddElementEvent;
import com.redknee.service.event.BranchCreateEvent;
import com.redknee.service.event.LabelCreateEvent;
import com.redknee.service.event.LabelRemoveEvent;
import com.redknee.service.event.ModifyElementEvent;
import com.redknee.service.event.RemoveElementEvent;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.event.ValidationEvent;
import com.redknee.util.Constants;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        if (StringUtils.isBlank(ref)) {
            log.warn("Ref is empty. Returning without further processing");
            return;
        }
        log.info("Try to handle ref {}", ref);
        if (Constants.MASTER_BRANCH.equals(ref)) {
            handlePushCommit(event);
        } else if (ref.startsWith(Constants.TAG_REF_STARTS_WITH)) {
            handleTagCommit(event);
        } else if (ref.startsWith(Constants.BRANCH_REF_STARTS_WITH)) {
            handleBranchCommit(event);
        } else {
            log.info("Event ref {} is not handled", ref);
        }
    }

    private void handleBranchCommit(EventDto event) {
        publisher.publishEvent(new ValidationEvent(event));
        String[] branchSplits = event.getRef().split("/");
        String branch = branchSplits[branchSplits.length - 1];
        if (!event.getDeleted()) {
            Commit headCommit = event.getHeadCommit();
            publisher.publishEvent(
                    new BranchCreateEvent(event.getCreated(), branch, event.getRepository().getFullName(),
                            headCommit.getAdded(), headCommit.getModified()));
        }
    }

    private void handleTagCommit(EventDto event) {
        publisher.publishEvent(new ValidationEvent(event));
        String[] tagSplits = event.getRef().split("/");
        String tag = tagSplits[tagSplits.length - 1];
        if (event.getCreated()) {
            // tag created
            Commit headCommit = event.getHeadCommit();
            publisher.publishEvent(new LabelCreateEvent(tag, event.getRepository().getFullName(), headCommit.getAdded(),
                    headCommit.getModified()));
        }
        if (event.getDeleted()) {
            // tag deleted
            publisher.publishEvent(new LabelRemoveEvent(tag, event.getRepository().getFullName()));
        }
    }

    private void handlePushCommit(EventDto event) {
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
                                event.getDeliveryId(), attachCommitIdToMessage(commit), modified));
            }

            if (!CollectionUtils.isEmpty(commit.getAdded())) {
                publisher.publishEvent(
                        new AddElementEvent(repository.getFullName(), repository.getName(), repository.getId(),
                                event.getDeliveryId(), attachCommitIdToMessage(commit), commit.getAdded()));
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
