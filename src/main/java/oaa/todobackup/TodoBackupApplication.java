package oaa.todobackup;

import oaa.todobackup.configuration.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {
        AsyncConfiguration.class,
        CoreConfiguration.class,
        FeignConfiguration.class,
        JobConfiguration.class,
        MongoConfiguration.class})
public class TodoBackupApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoBackupApplication.class, args);
    }
}
