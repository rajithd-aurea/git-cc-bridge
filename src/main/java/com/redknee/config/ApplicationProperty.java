package com.redknee.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class ApplicationProperty {

    private Map<String, String> pathMapper;
    private ClearCase clearCase;
    private GitServer gitServer;

    @Getter
    @Setter
    public static class ClearCase {

        private String viewName;
        private Server server;
    }

    @Getter
    @Setter
    public static class Server {

        private String hostname;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class GitServer {

        private String username;
        private String password;
        private String workspace;
    }

}
