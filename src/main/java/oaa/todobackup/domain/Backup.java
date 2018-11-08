package oaa.todobackup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;

/**
 * Represent info about specific backup (does not include backup data)
 */
@Data
@Builder(toBuilder = true)
public class Backup {
    @Id
    String id;
    String date;
    Status status;

    @AllArgsConstructor
    public enum Status {
        IN_PROGRESS("In progress"),
        FAILED("Failed"),
        OK("OK");

        @Getter
        private String name;
    }
}
