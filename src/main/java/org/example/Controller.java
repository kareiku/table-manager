package org.example;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.stream.StreamSupport;

public class Controller {
    private Table original;
    private Table modified;

    private enum SortValue {UNSORTED, ASC, DESC}

    private static final SortValue[] sortValues = SortValue.values();
    private SortValue currentSortValue;

    public Controller() {
        this.currentSortValue = sortValues[0];
    }

    public Table loadSheet(Sheet sheet) {
        this.original = new Table(StreamSupport
                .stream(sheet.spliterator(), false)
                .map(row -> StreamSupport.stream(row.spliterator(), false).map(Object::toString).toArray(String[]::new))
                .toList().toArray(String[][]::new));
        return new Table(this.original);
    }

    public void sort(String field) {
        this.modified = switch (this.currentSortValue) {
            case UNSORTED -> this.original.getSorted(field, false);
            case ASC -> this.original.getSorted(field, true);
            case DESC -> new Table(this.original);
        };
        this.currentSortValue = sortValues[(this.currentSortValue.ordinal() + 1) % sortValues.length];
    }
}
