package org.example.tablemanager;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.stream.StreamSupport;

public class Controller {
    private Table table;

    public void loadSheet(Sheet sheet) {
        this.table = new Table(StreamSupport
                .stream(sheet.spliterator(), false)
                .map(row -> StreamSupport.stream(row.spliterator(), false).map(Object::toString).toArray(String[]::new))
                .toList().toArray(String[][]::new));
    }
}
