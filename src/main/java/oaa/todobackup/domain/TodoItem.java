package oaa.todobackup.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Value;

/**
 * Represent info about TodoItem prepared for export
 */
@Value
@Builder
public class TodoItem {
    public static final String HEADER = "Username;TodoItemId;Subject;DueDate;Done\n";

    @CsvBindByPosition(position = 1)
    long todoItemId;

    @CsvBindByPosition(position = 0)
    String username;

    @CsvBindByPosition(position = 2)
    String subject;

    @CsvBindByPosition(position = 3)
    String dueDate;

    @CsvBindByPosition(position = 4)
    boolean done;
}
