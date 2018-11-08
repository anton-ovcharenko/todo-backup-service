package oaa.todobackup.job

import oaa.todobackup.domain.Backup
import oaa.todobackup.repository.BackupRepository
import oaa.todobackup.service.dataloader.BackupDataLoaderService
import spock.lang.Specification
import spock.lang.Unroll

import static oaa.todobackup.domain.Backup.Status.FAILED

class StuckBackupStatusUpdateJobTest extends Specification {

    BackupDataLoaderService backupDataLoaderService = Mock()
    BackupRepository backupRepository = Mock()

    StuckBackupStatusUpdateJob backupStatusUpdateJob =
            new StuckBackupStatusUpdateJob(backupDataLoaderService, backupRepository)

    def "job do nothing when there are no IN_PROGRESS backups"() {
        when:
        backupStatusUpdateJob.updateStatusForStuckBackups()

        then:
        1 * backupRepository.findAllByStatus(Backup.Status.IN_PROGRESS) >>
                []
        0 * _
    }

    def "job do nothing when all found IN_PROGRESS backups are active"() {
        given:
        def backup = buildBackup("1")
        when:
        backupStatusUpdateJob.updateStatusForStuckBackups()

        then:
        1 * backupRepository.findAllByStatus(Backup.Status.IN_PROGRESS) >> [backup]
        1 * backupDataLoaderService.getActiveBackupIds() >> ([backup.id] as Set)
        0 * _
    }

    @Unroll
    def "job do nothing when not all IN_PROGRESS backups are active and after #description"() {
        given:
        def backup1 = buildBackup("1")
        def backup2 = buildBackup("2")
        when:
        backupStatusUpdateJob.updateStatusForStuckBackups()

        then:
        2 * backupRepository.findAllByStatus(Backup.Status.IN_PROGRESS) >>> [[backup1, backup2], inProgressAfter]
        1 * backupDataLoaderService.getActiveBackupIds() >> (activeSet as Set)
        0 * _

        where:
        description                               | activeSet | inProgressAfter
        "they change status"                      | []        | []
        "then they change status and new appears" | ["1"]     | [buildBackup("3")]
    }

    @Unroll
    def "job should change status of stuck backups on FAILED when #description"() {
        given:
        def backup1 = buildBackup("1")
        def backup2 = buildBackup("2")
        when:
        backupStatusUpdateJob.updateStatusForStuckBackups()

        then:
        2 * backupRepository.findAllByStatus(Backup.Status.IN_PROGRESS) >>>
                [[backup1, backup2], inProgressAfter]
        1 * backupDataLoaderService.getActiveBackupIds() >> (activeSet as Set)
        1 * backupRepository.saveAll(updatedList) >> []
        0 * _

        where:
        description                     | activeSet | inProgressAfter    | updatedList
        "first does not change status"  | []        | [buildBackup("1")] | [buildBackup("1", FAILED)]
        "second does not change status" | ["1"]     | [buildBackup("2")] | [buildBackup("2", FAILED)]
    }

    private Backup buildBackup(String id, Backup.Status status = Backup.Status.IN_PROGRESS) {
        Backup.builder()
                .id(id)
                .status(status)
                .build()
    }
}
