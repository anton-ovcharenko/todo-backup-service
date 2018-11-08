package oaa.todobackup.configuration;

import oaa.todobackup.domain.TodoItem;
import oaa.todobackup.exporter.BatchedCsvTodoItemWriterExporter;
import oaa.todobackup.exporter.WriterExporter;
import oaa.todobackup.integration.todoitemserver.TodoItemServerClient;
import oaa.todobackup.repository.BackupDataBatchRepository;
import oaa.todobackup.repository.BackupRepository;
import oaa.todobackup.service.backup.BackupService;
import oaa.todobackup.service.backup.DefaultBackupService;
import oaa.todobackup.service.dataloader.AsyncBackupDataLoaderService;
import oaa.todobackup.service.dataloader.BackupDataLoaderService;
import oaa.todobackup.service.export.DefaultExportService;
import oaa.todobackup.service.export.ExportService;
import oaa.todobackup.service.export.ExportService.ExportFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;

@Configuration
public class CoreConfiguration {

    @Value("${cvsExporter.batchSize}")
    private int cvsExporterBatchSize;

    @Value("${backupData.batchSize}")
    private int backupDataBatchSize;

    @Bean
    public BackupService backupService(BackupRepository backupRepository,
                                       BackupDataLoaderService backupDataLoader) {
        return new DefaultBackupService(Clock.systemDefaultZone(), backupRepository, backupDataLoader);
    }

    @Bean
    public BackupDataLoaderService backupDataLoaderService(BackupRepository backupRepository,
                                                           TodoItemServerClient todoItemServerClient,
                                                           BackupDataBatchRepository backupDataBatchRepository) {
        return new AsyncBackupDataLoaderService(
                todoItemServerClient,
                backupRepository,
                backupDataBatchRepository,
                backupDataBatchSize);
    }

    @Bean
    public ExportService exportService(BackupRepository backupRepository,
                                       BackupDataBatchRepository backupDataBatchRepository) {
        Map<ExportFormat, WriterExporter<TodoItem>> exporterHolder = new EnumMap<>(ExportFormat.class);
        exporterHolder.put(ExportFormat.CSV, new BatchedCsvTodoItemWriterExporter(cvsExporterBatchSize));
        return new DefaultExportService(backupRepository, backupDataBatchRepository, exporterHolder);
    }
}
