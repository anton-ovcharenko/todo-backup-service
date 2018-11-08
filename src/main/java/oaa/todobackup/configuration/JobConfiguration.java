package oaa.todobackup.configuration;

import oaa.todobackup.job.StuckBackupStatusUpdateJob;
import oaa.todobackup.repository.BackupRepository;
import oaa.todobackup.service.dataloader.BackupDataLoaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class JobConfiguration {

    @Bean
    public StuckBackupStatusUpdateJob stuckBackupStatusUpdateJob(BackupRepository backupRepository,
                                                                 BackupDataLoaderService backupDataLoaderService) {
        return new StuckBackupStatusUpdateJob(backupDataLoaderService, backupRepository);
    }
}
