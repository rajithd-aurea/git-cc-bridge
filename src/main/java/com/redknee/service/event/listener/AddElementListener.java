package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.AddElementEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddElementListener extends AbstractListener {

    AddElementListener(ApplicationProperty applicationProperty,
            ClearCaseCommandExecutor clearCaseCommandExecutor, ClearCaseVobMapper clearCaseVobMapper) {
        super(applicationProperty, clearCaseVobMapper, clearCaseCommandExecutor);
    }

    @EventListener
    public void handle(AddElementEvent event) {
        log.info("Trying to handle add new elements event for repo name {} and delivery id {}", event.getRepoFullName(),
                event.getDeliveryId());
        handleAddAndModifyEvent(event);
    }
}
