package oaa.todobackup.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.domain.Backup;
import oaa.todobackup.repository.BackupRepository;
import oaa.todobackup.service.dataloader.BackupDataLoaderService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Job that is used for detecting stuck backups in status IN_PROGRESS and mark them as FAILED
 * It can be possible when updating status OK or FAILED was interrupted with error
 */
@Slf4j
@AllArgsConstructor
public class StuckBackupStatusUpdateJob {

    private BackupDataLoaderService backupDataLoaderService;
    private BackupRepository backupRepository;

    @Scheduled(fixedRateString = "${updateStatusForStuckBackups.fixed.rate.ms}")
    public void updateStatusForStuckBackups() {
        log.debug("Start checking on stuck backups");
        //all backups in status IN_PROGRESS
        List<Backup> inProgressBackupsBefore = findInProgressBackups();
        if (!inProgressBackupsBefore.isEmpty()) {
            //all active backups
            Set<String> activeBackupIds = backupDataLoaderService.getActiveBackupIds();
            //all IN_PROGRESS backups that is not active
            Set<String> possiblyStuckBackupIds = inProgressBackupsBefore.stream()
                    .map(Backup::getId)
                    .filter(backupId -> !activeBackupIds.contains(backupId))
                    .collect(Collectors.toSet());
            if (!possiblyStuckBackupIds.isEmpty()) {
                //all backups in status IN_PROGRESS again
                List<Backup> inProgressBackupsAfter = findInProgressBackups();
                //stuck backups that which status is still IN_PROGRESS
                List<Backup> stuckBackups = inProgressBackupsAfter.stream()
                        .filter(backup -> possiblyStuckBackupIds.contains(backup.getId()))
                        .map(StuckBackupStatusUpdateJob::updateStatus)
                        .collect(Collectors.toList());
                if (!stuckBackups.isEmpty()) {
                    backupRepository.saveAll(stuckBackups);
                    log.debug("Stuck backups will be updated on failed: {}", stuckBackups);
                }
            }
        }
    }

    private static Backup updateStatus(Backup backup) {
        return backup.toBuilder()
                .status(Backup.Status.FAILED)
                .build();
    }

    private List<Backup> findInProgressBackups() {
        return backupRepository.findAllByStatus(Backup.Status.IN_PROGRESS);
    }
}
