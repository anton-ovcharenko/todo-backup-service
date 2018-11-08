package oaa.todobackup.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class Utils {

    /**
     * Split list of <code>T</code> on few lists witch size is not more than <code>size</code>
     *
     * @param originalList list of object to split, not null
     * @param size         maximum size of split list
     * @param <T>          class of object which list will be split
     * @return split list of lists of objects
     * @throws NullPointerException     if <code>originalList</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>size</code> is less or equals zero
     */
    public static <T> List<List<T>> splitListBySize(List<T> originalList, int size) {
        Objects.requireNonNull(originalList, "OriginalList can not be null");
        if (size <= 0) {
            throw new IllegalArgumentException("Size should be positive");
        }

        List<List<T>> lists = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += size) {
            int end = Math.min(originalList.size(), i + size);
            lists.add(originalList.subList(i, end));
        }
        return lists;
    }
}
