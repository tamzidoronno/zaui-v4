package com.thundashop.core.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    private static final int processors = Runtime.getRuntime().availableProcessors();

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        // Default thread pool.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setThreadNamePrefix("taskExecutor-default-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "backupServerSyncExecutor")
    public TaskExecutor backupServerSyncExecutor() {
        // Setting core and max pool size 1 ensure that at any moment only one task is executed.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("backup-server-sync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "mailSenderExecutor")
    public TaskExecutor mailSenderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setThreadNamePrefix("mail-sender-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "grafanaFeederExecutor")
    public TaskExecutor grafanaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setThreadNamePrefix("grafana-feeder-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "pingServerExecutor")
    public TaskExecutor pingServerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setThreadNamePrefix("ping-server-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "webManagerExecutor")
    public TaskExecutor webManagerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(processors);
        executor.setThreadNamePrefix("web-manager-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
