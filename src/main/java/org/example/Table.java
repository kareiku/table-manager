package org.example;

import java.util.Arrays;
import java.util.Collections;

public class Table {
    private final String[][] table;

    public Table(String[][] table) {
        this.table = table;
    }

    public Table(Table table) {
        this.table = new String[table.table.length][];
        for (int i = 0; i < table.table.length; i++) {
            this.table[i] = Arrays.copyOf(table.table[i], table.table[i].length);
        }
    }

    public String[][] table() {
        return this.table;
    }

    public Table getSorted(String field, boolean desc) {
        int fieldIndex = -1;
        int currentFieldIndex = 0;
        for (String[] column : this.table) {
            if (column[0].equals(field)) {
                fieldIndex = currentFieldIndex;
            }
            currentFieldIndex++;
        }
        if (fieldIndex != -1) {
            Table sorted = new Table(this.table);
            if (desc) {
                Arrays.sort(sorted.table[fieldIndex], Collections.reverseOrder());
            } else {
                Arrays.sort(sorted.table[fieldIndex]);
            }
            return sorted;
        }
        return null;
    }

    // TODO
    public Table getFiltered(String field) {
        return null;
    }
}
