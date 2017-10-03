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

    @Getter
    @Setter
    public static class ClearCase {

        private String viewName;
        private Server server;
    }

    @Getter
    @Setter
    public static class Server {

        private String ip;
        private String username;
        private String password;
    }

}
