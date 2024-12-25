package org.example;

import java.util.Arrays;

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

    // TODO
    public Table getSorted(int fieldNumber) {
        return null;
    }

    // TODO
    public Table getFiltered(int fieldNumber) {
        return null;
    }
}
