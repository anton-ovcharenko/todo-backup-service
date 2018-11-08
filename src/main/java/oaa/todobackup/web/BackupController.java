package oaa.todobackup.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.exception.BackupNotFoundException;
import oaa.todobackup.exception.TodoBackupException;
import oaa.todobackup.exception.WrongBackupStatusException;
import oaa.todobackup.service.backup.BackupService;
import oaa.todobackup.service.export.ExportService;
import oaa.todobackup.service.export.ExportService.ExportFormat;
import oaa.todobackup.web.model.BackupResponse;
import oaa.todobackup.web.model.StartBackupResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RestController()
@Slf4j
@AllArgsConstructor
public class BackupController {

    private BackupService backupService;
    private ExportService exportService;

    @PostMapping(value = "/backups")
    public StartBackupResponse startBackup() {
        log.debug("Call createBackup method");
        return StartBackupResponse.builder()
                .backupId(backupService.createBackup())
                .build();
    }

    @GetMapping(value = "/backups")
    public List<BackupResponse> getBackups() {
        log.debug("Call getBackups method");
        return backupService.getBackups()
                .stream()
                .map(BackupResponse::fromBackup)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/exports/{backupId}")
    public void exportBackupsToCsv(@PathVariable("backupId") String backupId, HttpServletResponse response)
            throws IOException, BackupNotFoundException, WrongBackupStatusException {
        log.debug("Call exportBackupsToCsv method with backupId: {}", backupId);
        response.addHeader("Content-disposition", format("attachment;filename=backup_%s.csv", backupId));
        response.setContentType("text/csv");
        PrintWriter writer = response.getWriter();
        exportService.exportTo(writer, backupId, ExportFormat.CSV);
        writer.close();
    }

    @ExceptionHandler(TodoBackupException.class)
    public String exceptionHandler(TodoBackupException exception) {
        log.error("TodoBackupException appears in BackupController: " + exception.getMessage(), exception);
        return exception.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(reason = "Unexpected error, please try later")
    public void exceptionHandler(Exception exception) {
        log.error("Exception appears in BackupController: " + exception.getMessage(), exception);
    }
}
