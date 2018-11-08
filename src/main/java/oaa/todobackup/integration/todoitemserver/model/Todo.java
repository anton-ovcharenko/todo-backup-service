package oaa.todobackup.integration.todoitemserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {
    private long id;
    private String subject;
    private String dueDate;
    private boolean done;
}
