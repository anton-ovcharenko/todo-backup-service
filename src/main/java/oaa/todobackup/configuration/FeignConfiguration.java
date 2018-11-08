package oaa.todobackup.configuration;

import feign.Logger;
import oaa.todobackup.integration.todoitemserver.TodoItemServerClient;
import oaa.todobackup.integration.todoitemserver.TodoItemServerClientFallback;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = TodoItemServerClient.class)
public class FeignConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public TodoItemServerClientFallback todoItemServerClientFallback() {
        return new TodoItemServerClientFallback();
    }
}
