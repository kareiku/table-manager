package org.example;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.stream.StreamSupport;

public class Controller {
    private Table original;
    private Table modified;

    public Table loadSheet(Sheet sheet) {
        this.original = new Table(StreamSupport
                .stream(sheet.spliterator(), false)
                .map(row -> StreamSupport.stream(row.spliterator(), false).map(Object::toString).toArray(String[]::new))
                .toList().toArray(String[][]::new));
        return new Table(this.original);
    }

    // TODO
    public void sort(String fieldName) {
        this.modified = this.original.getSorted(0 /* fixme */);
    }
}
