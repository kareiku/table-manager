package org.example.tablemanager;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

public class Controller {
    private static Controller controller;

    private Controller() {
    }

    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public Workbook getWorkbook(File file) {
        return null; // TODO
    }
}
