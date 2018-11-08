package oaa.todobackup.exporter;

import java.io.Writer;
import java.util.List;

/**
 * Interface for exporter that will be write data directly in <code>Writer</code> object
 *
 * @param <T> class of objects that will be used during export process
 */
public interface WriterExporter<T> {

    /**
     * Start exporting process of specified items to specified writer
     *
     * @param writer <code>Writer</code> object
     * @param items  list of items that should be exported
     */
    void export(Writer writer, List<T> items);
}
