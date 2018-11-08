package oaa.todobackup.exporter;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oaa.todobackup.domain.TodoItem;
import oaa.todobackup.util.Utils;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

/**
 * CSV Exporter implementation that will be used intermediate flushing of data into <code>Writer</code>
 * After finishing <code>Writer</code> will NOT be closed
 */
@Slf4j
@AllArgsConstructor
public class BatchedCsvTodoItemWriterExporter implements WriterExporter<TodoItem> {

    private int batchSize;

    /**
     * Export process to <code>Writer</code> in CSV format
     *
     * @param writer <code>Writer</code> object, not null
     * @param items  list of items that should be exported, not null
     * @throws NullPointerException if either <code>writer</code> or <code>items</code> is <code>null</code>
     */
    @Override
    public void export(Writer writer, List<TodoItem> items) {
        Objects.requireNonNull(writer, "Writer can not be null");
        Objects.requireNonNull(items, "Items can not be null");

        try {
            StatefulBeanToCsv beanToCsv = createStatefulBeanToCsv(writer);
            writer.write(TodoItem.HEADER);
            for (List<TodoItem> batch : Utils.splitListBySize(items, batchSize)) {
                beanToCsv.write(batch);
                writer.flush();
            }
        } catch (CsvException | IOException e) {
            throw new RuntimeException("CSV export was failed", e);
        }
    }

    private StatefulBeanToCsv createStatefulBeanToCsv(Writer writer) {
        return new StatefulBeanToCsvBuilder(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .build();
    }
}
