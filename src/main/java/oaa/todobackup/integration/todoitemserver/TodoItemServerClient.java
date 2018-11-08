package oaa.todobackup.integration.todoitemserver;

import oaa.todobackup.integration.todoitemserver.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(name = "todoitemserver",
        url = "${todoitemserver.url}",
        fallback = TodoItemServerClientFallback.class)
public interface TodoItemServerClient {

    @RequestMapping(method = GET, value = "/users", produces = "application/json")
    List<User> gerUsers();
}
