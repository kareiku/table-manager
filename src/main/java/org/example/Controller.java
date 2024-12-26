package org.example;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Table original;
    private Table modified;

    private enum SortValue {UNSORTED, ASC, DESC}

    private static final SortValue[] sortValues = SortValue.values();
    private SortValue currentSortValue;

    public Table loadSheet(Sheet sheet) {
        this.currentSortValue = sortValues[0];
        List<List<String>> rows = new ArrayList<>();
        sheet.forEach(row -> {
            List<String> cells = new ArrayList<>();
            row.forEach(cell -> cells.add(cell.toString()));
            rows.add(cells);
        });
        this.original = new Table(rows);
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
