package oaa.todobackup.repository;

import oaa.todobackup.domain.Backup;
import oaa.todobackup.domain.Backup.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BackupRepository extends MongoRepository<Backup, String> {

    List<Backup> findAllByStatus(Status status);
}
