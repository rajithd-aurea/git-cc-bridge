package com.redknee.service.event.listener;

import com.redknee.config.ApplicationProperty;
import com.redknee.rest.dto.EventDto;
import com.redknee.service.event.ValidationEvent;
import com.redknee.service.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ValidationListener {

    private final ApplicationProperty applicationProperty;

    ValidationListener(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    @EventListener
    public void handle(ValidationEvent validationEvent) {
        EventDto eventDto = validationEvent.getEventDto();
        String vobPath = applicationProperty.getPathMapper().get(eventDto.getRepository().getFullName());
        if (StringUtils.isBlank(vobPath)) {
            throw new ApiException();
        }
    }

}
