package oaa.todobackup.integration.todoitemserver;

import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.integration.todoitemserver.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for TodoItemServerClient that returns empty list in case of error
 */
@Slf4j
public class TodoItemServerClientFallback implements TodoItemServerClient {

    @Override
    public List<User> gerUsers() {
        log.warn("Something wrong with TodoItemServer during call gerUsers, empty list will be used as fallback");
        return Collections.emptyList();
    }
}
