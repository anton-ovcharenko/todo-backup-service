package oaa.todobackup.service.export;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.domain.Backup;
import oaa.todobackup.domain.BackupDataBatch;
import oaa.todobackup.domain.TodoItem;
import oaa.todobackup.exception.BackupNotFoundException;
import oaa.todobackup.exception.WrongBackupStatusException;
import oaa.todobackup.exporter.WriterExporter;
import oaa.todobackup.repository.BackupDataBatchRepository;
import oaa.todobackup.repository.BackupRepository;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Default implementation of ExportService that implements factory for existed WriterExporter
 */
@Slf4j
@AllArgsConstructor
public class DefaultExportService implements ExportService {

    private BackupRepository backupRepository;
    private BackupDataBatchRepository backupDataBatchRepository;
    private Map<ExportFormat, WriterExporter<TodoItem>> exporterHolder;

    /**
     * Start export of backup to <code>writer</code> in <code>exportFormat</code>
     * via found <code>WriterExporter</code>
     *
     * @param writer       <code>Writer</code> that will be used for exporting, not null
     * @param backupId     id of backup which data will be exported, not null
     * @param exportFormat export format, not null
     * @throws BackupNotFoundException    if backup by specified id was not found
     * @throws NullPointerException       if any of parameter is null
     * @throws IllegalStateException      in case <code>WriterExporter</code> for specified format was not found
     * @throws WrongBackupStatusException in case <code>Backup</code> has not OK status or
     */
    @Override
    public void exportTo(Writer writer, String backupId, ExportFormat exportFormat)
            throws BackupNotFoundException, WrongBackupStatusException {
        Objects.requireNonNull(writer, "Writer can not be null");
        Objects.requireNonNull(backupId, "BackupId can not be null");
        Objects.requireNonNull(exportFormat, "ExportFormat can not be null");

        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new BackupNotFoundException(
                        format("Backup with id: %s was not found", backupId)));
        validateBackupStatus(backup);

        List<TodoItem> todoItems = backupDataBatchRepository.findByBackupId(backupId)
                .stream()
                .map(BackupDataBatch::getTodoItems)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        getExporter(exportFormat).export(writer, todoItems);
    }

    /**
     * Find <code>WriterExporter</code> by specified exportFormat
     */
    private WriterExporter<TodoItem> getExporter(ExportFormat exportFormat) {
        if (!exporterHolder.containsKey(exportFormat)) {
            throw new IllegalStateException(
                    format("Exporter by format: %s was not found", exportFormat.toString()));
        }
        return exporterHolder.get(exportFormat);
    }

    /**
     * Check that backup has valid status (OK) for exporting
     */
    private static void validateBackupStatus(Backup backup) throws WrongBackupStatusException {
        if (backup.getStatus() != Backup.Status.OK) {
            throw new WrongBackupStatusException(
                    format("Backup with id: %s can not be exported because it has status: %s", backup.getId(), backup.getStatus()));
        }
    }
}
