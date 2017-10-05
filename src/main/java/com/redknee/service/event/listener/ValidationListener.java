package com.redknee.service.event.listener;

import com.redknee.config.ApplicationProperty;
import com.redknee.config.ClearCaseVobMapper;
import com.redknee.rest.dto.EventDto;
import com.redknee.service.event.ValidationEvent;
import com.redknee.service.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationListener {

    private final ApplicationProperty applicationProperty;
    private final ClearCaseVobMapper clearCaseVobMapper;

    ValidationListener(ApplicationProperty applicationProperty, ClearCaseVobMapper clearCaseVobMapper) {
        this.applicationProperty = applicationProperty;
        this.clearCaseVobMapper = clearCaseVobMapper;
    }

    @EventListener
    public void handle(ValidationEvent validationEvent) {
        EventDto eventDto = validationEvent.getEventDto();
        String fullName = eventDto.getRepository().getFullName();
        String vobPath = clearCaseVobMapper.getPathMapper().get(fullName);
        if (StringUtils.isBlank(vobPath)) {
            log.info("VOB path is not exists for {} repository. Please configure in application.yml", fullName);
            throw new ApiException(String.format("[Validation Failed]: No VOB found for repository %s", fullName));
        }
    }

}
