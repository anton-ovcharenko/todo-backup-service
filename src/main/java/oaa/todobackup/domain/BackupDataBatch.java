package oaa.todobackup.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Represent data of some batch of backup.
 * Data from one backup can be split among few <code>BackupDataBatch</code>
 */
@Data
@Builder(toBuilder = true)
public class BackupDataBatch {
    @Id
    String id;
    String backupId;
    List<TodoItem> todoItems;
}
