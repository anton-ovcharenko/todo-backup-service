package oaa.todobackup.web.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StartBackupResponse {
    String backupId;
}
