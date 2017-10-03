package com.redknee.service;

import com.redknee.config.ApplicationProperty;
import com.redknee.rest.dto.EventDto;
import com.redknee.rest.dto.EventDto.Commit;
import com.redknee.util.Constants;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventHandler {

    @Autowired
    private ApplicationProperty applicationProperty;

    public void handleMessage(EventDto event) {
        String ref = event.getRef();
        if (!Constants.MASTER_BRANCH.equals(ref)) {
            return;
        }
        String vobPath = applicationProperty.getPathMapper().get(event.getRepository().getFullName());
        if (StringUtils.isBlank(vobPath)) {
            return;
        }
        List<Commit> commits = event.getCommits();
        for (Commit commit : commits) {

        }

    }
}
