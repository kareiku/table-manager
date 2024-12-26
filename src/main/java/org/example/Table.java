package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {
    private final List<List<String>> rows;

    private Table() {
        this.rows = new ArrayList<>();
    }

    @SafeVarargs
    public Table(List<String>... rows) {
        this();
        this.rows.addAll(Arrays.asList(rows));
    }

    public Table(List<List<String>> rows) {
        this();
        this.rows.addAll(rows);
    }

    public Table(Table table) {
        this();
        this.rows.addAll(table.rows);
    }

    public List<List<String>> toList() {
        return new ArrayList<>(this.rows);
    }

    // fixme
    public Table getSorted(String field, boolean desc) {
//        int fieldIndex = -1;
//        int currentFieldIndex = 0;
//        for (StringProperty[] column : this.table) {
//            if (column[0].getName().equals(field)) {
//                fieldIndex = currentFieldIndex;
//            }
//            currentFieldIndex++;
//        }
//        if (fieldIndex != -1) {
//            Table sorted = new Table(this);
//            if (desc) {
//                Arrays.sort(sorted.table[fieldIndex], Collections.reverseOrder());
//            } else {
//                Arrays.sort(sorted.table[fieldIndex]);
//            }
//            return sorted;
//        }
        return null;
    }

    // TODO
    public Table getFiltered(String field) {
        return null;
    }
}
