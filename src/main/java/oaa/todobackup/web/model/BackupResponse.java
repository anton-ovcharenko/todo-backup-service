package oaa.todobackup.web.model;

import lombok.Builder;
import lombok.Value;
import oaa.todobackup.domain.Backup;

@Value
@Builder
public class BackupResponse {
    private String backupId;
    private String date;
    private String status;

    public static BackupResponse fromBackup(Backup backup) {
        return BackupResponse.builder()
                .backupId(backup.getId())
                .status(backup.getStatus().getName())
                .date(backup.getDate())
                .build();
    }
}
