package com.redknee.service;

import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
import com.redknee.rest.dto.EventDto.Repository;
import com.redknee.service.event.ModifyElementEvent;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.event.ValidationEvent;
import com.redknee.util.Constants;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
            return;
        }
        Repository repository = event.getRepository();
        publisher.publishEvent(new ValidationEvent(event));
        publisher.publishEvent(new SourceCodeEvent(repository.getUrl(), repository.getName(), repository.getId()));
        if (isModifyFilesExists(event)) {
            publisher.publishEvent(new ModifyElementEvent(event));
        }

    }

    private boolean isModifyFilesExists(EventDto event) {
        List<Commit> commits = event.getCommits();
        for (Commit commit : commits) {
            List<String> modified = commit.getModified();
            if (!modified.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
