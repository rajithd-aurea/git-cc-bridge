package com.redknee.cc;

import com.redknee.config.ApplicationProperty;
import com.redknee.config.ApplicationProperty.Server;
import com.redknee.external.SshConnectionManager;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClearCaseCommandExecutor {

    private final ApplicationProperty applicationProperty;

    public ClearCaseCommandExecutor(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    public void executeCommand(List<String> commands) {
        Server server = applicationProperty.getClearCase().getServer();
        SshConnectionManager sshConnectionManager = new SshConnectionManager(server.getUsername(), server.getPassword(),
                server.getHostname());
        sshConnectionManager.executeCommands(commands);

    }

}
