package oaa.todobackup.service.export

import oaa.todobackup.domain.Backup
import oaa.todobackup.domain.BackupDataBatch
import oaa.todobackup.domain.TodoItem
import oaa.todobackup.exception.BackupNotFoundException
import oaa.todobackup.exception.WrongBackupStatusException
import oaa.todobackup.exporter.WriterExporter
import oaa.todobackup.repository.BackupDataBatchRepository
import oaa.todobackup.repository.BackupRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static oaa.todobackup.service.export.ExportService.ExportFormat.CSV

class DefaultExportServiceTest extends Specification {

    BackupRepository backupRepository = Mock()
    BackupDataBatchRepository backupDataBatchRepository = Mock()
    Map<ExportService.ExportFormat, WriterExporter<TodoItem>> exporterHolder = Mock()

    DefaultExportService service =
            new DefaultExportService(backupRepository, backupDataBatchRepository, exporterHolder)

    @Shared
    Writer writer = Mock()
    WriterExporter writerExporter = Mock()

    @Unroll
    def "exportTo should throw NPE in case of #parameterName is null"() {
        when:
        service.exportTo(writerObj, backupId, exportFormat)

        then:
        thrown NullPointerException

        where:
        parameterName  | writerObj | backupId | exportFormat
        "writer"       | null      | ""       | CSV
        "backupId"     | writer    | null     | CSV
        "exportFormat" | writer    | ""       | null
    }

    def "exportTo should throw BackupNotFoundException if backup was not found"() {
        given:
        def id = "some id"

        when:
        service.exportTo(writer, id, CSV)

        then:
        1 * backupRepository.findById(id) >> Optional.empty()
        then:
        thrown(BackupNotFoundException)
    }

    @Unroll
    def "exportTo should throw WrongBackupStatusException if backup status is #status"() {
        given:
        def id = "some id"

        when:
        service.exportTo(writer, id, CSV)

        then:
        1 * backupRepository.findById(id) >>
                Optional.of(Backup.builder().status(status).build())
        then:
        thrown(WrongBackupStatusException)

        where:
        status << [Backup.Status.IN_PROGRESS, Backup.Status.FAILED]
    }

    def "exportTo should throw IllegalStateException if exporter was not found"() {
        given:
        def id = "some id"

        when:
        service.exportTo(writer, id, CSV)

        then:
        thrown(IllegalStateException)
        1 * backupRepository.findById(id) >>
                Optional.of(Backup.builder().status(Backup.Status.OK).build())
        1 * backupDataBatchRepository.findByBackupId(id) >> []
        1 * exporterHolder.containsKey(CSV) >> false
    }

    @Unroll
    def "exportTo call export for writerExporter when #description"() {
        given:
        def id = "some id"

        when:
        service.exportTo(writer, id, CSV)

        then:
        1 * backupRepository.findById(id) >>
                Optional.of(Backup.builder().status(Backup.Status.OK).build())
        1 * backupDataBatchRepository.findByBackupId(id) >> backupDataBatchList
        1 * exporterHolder.containsKey(CSV) >> true
        1 * exporterHolder.get(CSV) >> writerExporter
        1 * writerExporter.export(writer, dataToExport)

        where:
        description             | backupDataBatchList                              | dataToExport
        "there are no data"     | []                                               | []
        "one DataBatch exists"  | [buildBDB([buildTI(1), buildTI(2)])]             | [buildTI(1), buildTI(2)]
        "few DataBatches exist" | [buildBDB([buildTI(3)]), buildBDB([buildTI(4)])] | [buildTI(3), buildTI(4)]
    }

    private buildBDB(List<TodoItem> todoItems) {
        BackupDataBatch.builder()
                .todoItems(todoItems)
                .build()
    }

    private buildTI(long id) {
        TodoItem.builder()
                .todoItemId(id)
                .build()
    }
}
