package oaa.todobackup.service.backup;

import oaa.todobackup.domain.Backup;

import java.util.List;

/**
 * Interface for service that is used for creating new backup and getting list of all backups
 */
public interface BackupService {

    /**
     * Creating new backup
     *
     * @return backup id that was created
     */
    String createBackup();

    /**
     * Get all backups
     *
     * @return list of all backups in the system
     */
    List<Backup> getBackups();
}
