package oaa.todobackup.service.backup

import oaa.todobackup.domain.Backup
import oaa.todobackup.repository.BackupRepository
import oaa.todobackup.service.dataloader.BackupDataLoaderService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import static oaa.todobackup.domain.Backup.Status.IN_PROGRESS

class DefaultBackupServiceTest extends Specification {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    Clock clock = Clock.fixed(Instant.ofEpochSecond(3600), ZoneId.systemDefault())

    BackupRepository backupRepository = Mock()
    BackupDataLoaderService backupDataLoaderService = Mock()

    DefaultBackupService service = new DefaultBackupService(clock, backupRepository, backupDataLoaderService)

    @Unroll
    def "getBackups should return #description"() {
        when:
        def result = service.getBackups()

        then:
        result == ret
        1 * backupRepository.findAll() >> ret
        0 * _

        where:
        description          | ret
        "empty response"     | []
        "not empty response" | [Backup.builder().build()]
    }

    def "createBackup should save IN+PROGRESS backup and start loading"() {
        given:
        def backup = Backup.builder()
                .date(LocalDateTime.now(clock).format(dateTimeFormatter))
                .status(IN_PROGRESS)
                .build()
        def backupAfterSave = Backup.builder()
                .id("some id")
                .date(LocalDateTime.now(clock).format(dateTimeFormatter))
                .status(IN_PROGRESS)
                .build()

        when:
        def result = service.createBackup()

        then:
        result == backupAfterSave.id
        1 * backupRepository.save(backup) >> backupAfterSave
        1 * backupDataLoaderService.load(backupAfterSave.id)
        0 * _
    }
}

