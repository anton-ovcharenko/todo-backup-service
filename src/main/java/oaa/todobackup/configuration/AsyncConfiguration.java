package oaa.todobackup.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Value("${backupDataLoaderTaskExecutor.threads}")
    private int backupDataLoaderTaskExecutorThreads;

    @Bean(name = "backupDataLoaderTaskExecutor")
    public Executor backupDataLoaderTaskExecutor() {
        return Executors.newFixedThreadPool(backupDataLoaderTaskExecutorThreads);
    }
}