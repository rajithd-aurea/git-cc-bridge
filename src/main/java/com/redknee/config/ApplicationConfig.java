package com.redknee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ApplicationConfig {

    @Bean
    public AsyncTaskExecutor eventTaskExecutor() {
        return getThreadPoolTaskExecutor("EventT-");
    }

    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor(String name) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setThreadNamePrefix(name);
        return taskExecutor;
    }

}
