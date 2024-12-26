package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cell {
    private StringProperty value;

    public Cell(String s) {
        this.value = new SimpleStringProperty(this, s);
    }

    public String getValue() {
        return this.value.get();
    }

    public void setValue(String s) {
        this.value.setValue(s);
    }
}
