package oaa.todobackup.service.dataloader;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.domain.Backup;
import oaa.todobackup.domain.BackupDataBatch;
import oaa.todobackup.domain.TodoItem;
import oaa.todobackup.integration.todoitemserver.TodoItemServerClient;
import oaa.todobackup.integration.todoitemserver.model.User;
import oaa.todobackup.repository.BackupDataBatchRepository;
import oaa.todobackup.repository.BackupRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static oaa.todobackup.util.Utils.splitListBySize;

/**
 * Asynchronous implementation of <code>BackupDataLoaderService</code>
 */
@Slf4j
@AllArgsConstructor
public class AsyncBackupDataLoaderService implements BackupDataLoaderService {
    private final Set<String> activeBackupIds = ConcurrentHashMap.newKeySet(); // way to obtain ConcurrentHashSet

    private TodoItemServerClient todoItemServerClient;
    private BackupRepository backupRepository;
    private BackupDataBatchRepository backupDataBatchRepository;
    private int backupDataBatchSize;

    /**
     * Load backup data by specified backupId from <code>TodoItemServerClient</code>,
     * split data on <code>BackupDataBatch</code> considering <code>backupDataBatchSize</code>,
     * save <code>BackupDataBatch</code> and update status of <code>Backup</code>
     *
     * @param backupId backup id to load data, not null
     * @throws IllegalArgumentException if backup by specified id was not found
     * @throws NullPointerException     if backupId is null
     */
    @Override
    @Async("backupDataLoaderTaskExecutor")
    public void load(String backupId) {
        Objects.requireNonNull(backupId, "BackupId can not be null");
        log.info("Start loading data for backup with id: {} on thread: {}", backupId, currentThread().getName());

        try {
            activeBackupIds.add(backupId);
            List<TodoItem> todoItems = todoItemServerClient.gerUsers()
                    .stream()
                    .flatMap(AsyncBackupDataLoaderService::buildTodoItemStream)
                    .collect(Collectors.toList());

            List<BackupDataBatch> backupDataBatches = splitListBySize(todoItems, backupDataBatchSize)
                    .stream()
                    .map(batch -> buildBackupDataBatch(backupId, batch))
                    .collect(Collectors.toList());
            backupDataBatchRepository.saveAll(backupDataBatches);
            updateBackup(backupId, Backup.Status.OK);
            log.info("Loading backup by id: {} was finished successfully", backupId);
        } catch (Exception exception) {
            log.error("Loading backup by id: {} was failed", backupId, exception);
            updateBackup(backupId, Backup.Status.FAILED);
        } finally {
            activeBackupIds.remove(backupId);
        }
    }

    private void updateBackup(String backupId, Backup.Status status) {
        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new IllegalArgumentException(
                        format("Backup update failed. Backup with id: %s was not found", backupId)));

        Backup updatedBackup = backup.toBuilder()
                .status(status)
                .build();
        backupRepository.save(updatedBackup);
    }

    /**
     * Return ids of active backups
     *
     * @return set of active backup's ids
     */
    @Override
    public Set<String> getActiveBackupIds() {
        return activeBackupIds;
    }

    private static BackupDataBatch buildBackupDataBatch(String backupId, List<TodoItem> batch) {
        return BackupDataBatch.builder()
                .todoItems(batch)
                .backupId(backupId)
                .build();
    }

    private static Stream<TodoItem> buildTodoItemStream(User user) {
        return user.getTodos()
                .stream()
                .map(todo -> TodoItem.builder()
                        .todoItemId(todo.getId())
                        .username(user.getUsername())
                        .subject(todo.getSubject())
                        .dueDate(todo.getDueDate())
                        .done(todo.isDone())
                        .build());
    }
}
