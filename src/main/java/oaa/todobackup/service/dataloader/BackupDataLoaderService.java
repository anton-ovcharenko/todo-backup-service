package oaa.todobackup.service.dataloader;

import java.util.Set;

/**
 * Interface for service that is used for loading backup data
 */
public interface BackupDataLoaderService {

    /**
     * Load backup data for specified by id backup
     *
     * @param backupId backup id to load data
     */
    void load(String backupId);

    /**
     * Load active backups
     *
     * @return set pf backups that is in work now
     */
    Set<String> getActiveBackupIds();
}
