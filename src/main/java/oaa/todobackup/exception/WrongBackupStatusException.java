package oaa.todobackup.exception;

public class WrongBackupStatusException extends TodoBackupException {
    public WrongBackupStatusException(String message) {
        super(message);
    }
}
