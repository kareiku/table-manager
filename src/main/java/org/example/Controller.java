package org.example;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    public List<List<String>> loadSheet(Sheet sheet) {
        List<List<String>> rows = new ArrayList<>();
        sheet.forEach(row -> {
            List<String> cells = new ArrayList<>();
            row.forEach(cell -> cells.add(cell.toString()));
            rows.add(cells);
        });
        return rows;
    }
}
