package oaa.todobackup.service.backup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.domain.Backup;
import oaa.todobackup.repository.BackupRepository;
import oaa.todobackup.service.dataloader.BackupDataLoaderService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Default implementation of BackupService
 */
@Slf4j
@AllArgsConstructor
public class DefaultBackupService implements BackupService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Clock clock;
    private BackupRepository backupRepository;
    private BackupDataLoaderService backupDataLoaderService;

    /**
     * Create new backup in status IN_PROGRESS with current date
     *
     * @return created backup id
     */
    @Override
    public String createBackup() {
        Backup backup = Backup.builder()
                .date(LocalDateTime.now(clock).format(dateTimeFormatter))
                .status(Backup.Status.IN_PROGRESS)
                .build();
        Backup savedBackup = backupRepository.save(backup);
        log.debug("New backup: {} was created", savedBackup);
        backupDataLoaderService.load(savedBackup.getId());
        return savedBackup.getId();
    }

    /**
     * Return all backups
     *
     * @return list of backups
     */
    @Override
    public List<Backup> getBackups() {
        return backupRepository.findAll();
    }
}
