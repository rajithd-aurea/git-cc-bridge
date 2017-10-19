package com.redknee.service.event.listener;

import com.redknee.cc.ClearCaseCommandExecutor;
import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.service.event.ModifyElementEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ModifyElementListener extends AbstractListener {

    ModifyElementListener(ApplicationProperty applicationProperty, ClearCaseCommandExecutor clearCaseCommandExecutor,
            ClearCaseVobMapper clearCaseVobMapper) {
        super(applicationProperty, clearCaseVobMapper, clearCaseCommandExecutor);
    }

    @EventListener
    public void handle(ModifyElementEvent event) {
        log.info("Trying to handle modify elements event for repo name {} and delivery id {}", event.getRepoFullName(),
                event.getDeliveryId());
        handleAddAndModifyEvent(event);
    }
}
