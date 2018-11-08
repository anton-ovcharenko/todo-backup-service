package oaa.todobackup.repository;

import oaa.todobackup.domain.BackupDataBatch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BackupDataBatchRepository extends MongoRepository<BackupDataBatch, String> {

    List<BackupDataBatch> findByBackupId(String backupId);
}
