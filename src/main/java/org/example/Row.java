package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Row {
    private StringProperty[] columns;

    public Row(String... columns) {
        this.columns = new StringProperty[columns.length];
        for (int i = 0; i < columns.length; i++) {
            this.columns[i] = new SimpleStringProperty(this, columns[i]);
        }
    }

    public String get(int index) {
        return this.columns.length > index ? this.columns[index].get() : "";
    }
}
