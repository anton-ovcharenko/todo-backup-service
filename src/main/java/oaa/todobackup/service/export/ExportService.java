package oaa.todobackup.service.export;

import oaa.todobackup.exception.BackupNotFoundException;
import oaa.todobackup.exception.WrongBackupStatusException;

import java.io.Writer;

/**
 * Interface for service that is used for exporting backup to <code>Writer</code> in different formats
 */
public interface ExportService {

    /**
     * Perform export of backup to specified <code>Writer</code> in specified format
     *
     * @param writer       <code>Writer</code> that will be used for exporting
     * @param backupId     id of backup which data will be exported
     * @param exportFormat export format
     * @throws BackupNotFoundException    if backup by specified id was not found
     * @throws WrongBackupStatusException in case <code>Backup</code> has not OK status or
     */
    void exportTo(Writer writer, String backupId, ExportFormat exportFormat)
            throws BackupNotFoundException, WrongBackupStatusException;

    /**
     * Supported export formats
     */
    enum ExportFormat {
        CSV
    }
}
