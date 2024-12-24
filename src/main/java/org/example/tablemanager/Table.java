package org.example.tablemanager;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

// fixme please
public class Table {
    private Language LANG;
    private String[][] table;

    private Table(String[][] table) {
        this.table = table;
    }

    private File chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xls", "*.xlsx", "*.xlsm"));
        return fileChooser.showOpenDialog(stage);
    }

    private Sheet chooseSheet(Workbook workbook) {
        List<String> sheetNames = new ArrayList<>();
        workbook.iterator().forEachRemaining(sheet -> sheetNames.add(sheet.getSheetName()));

        ChoiceDialog<String> sheetDialog = new ChoiceDialog<>(sheetNames.get(0), sheetNames);
        sheetDialog.setTitle(LANG.get(Language.Key.Title));
        sheetDialog.setHeaderText(null);
        sheetDialog.setContentText(LANG.get(Language.Key.SelectSheet));
        Optional<String> sheetNameOptional = sheetDialog.showAndWait();

        return sheetNameOptional.map(workbook::getSheet).orElse(null);
    }

    private Table excelToTable(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = this.chooseSheet(workbook);
            return new Table(StreamSupport
                    .stream(sheet.spliterator(), false)
                    .map(row -> StreamSupport.stream(row.spliterator(), false).map(Object::toString).toArray(String[]::new))
                    .toList().toArray(String[][]::new));
        } catch (IOException ignore) {
            // this.showAlert(LANG.get(Language.Key.OpenError));
        }
        return null;
    }

    public void openFile(Stage stage) {
        this.excelToTable(this.chooseFile(stage));
    }
}
