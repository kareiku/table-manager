package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private record Filter(int columnIndex, String value) {
    }

    private final Language language;
    private final TableView<ObservableList<String>> tableView;
    private final ObservableList<ObservableList<String>> data;
    private final List<Filter> filterValues;

    public Controller(Language language, TableView<ObservableList<String>> tableView) {
        this.language = language;
        this.tableView = tableView;
        this.data = FXCollections.observableArrayList();
        this.filterValues = new ArrayList<>();
    }

    public List<List<String>> loadSheet(Sheet sheet) {
        List<List<String>> rows = new ArrayList<>();
        sheet.forEach(row -> {
            List<String> cells = new ArrayList<>();
            row.forEach(cell -> cells.add(cell.toString()));
            rows.add(cells);
        });
        return rows;
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(this.language.get(Language.Key.Title));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public File chooseOpenFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(this.language.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(this.language.get(Language.Key.ExcelFiles), "*.xls", "*.xlsx", "*.xlsm"));
        return fileChooser.showOpenDialog(stage);
    }

    public File chooseSaveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(this.language.get(Language.Key.SaveFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(this.language.get(Language.Key.ExcelFiles), "*.xlsx"));
        return fileChooser.showSaveDialog(stage);
    }

    public Sheet chooseSheet(Workbook workbook) {
        List<String> sheetNames = new ArrayList<>();
        workbook.iterator().forEachRemaining(sheet -> sheetNames.add(sheet.getSheetName()));

        ChoiceDialog<String> sheetDialog = new ChoiceDialog<>(sheetNames.get(0), sheetNames);
        sheetDialog.setTitle(this.language.get(Language.Key.Title));
        sheetDialog.setHeaderText(null);
        sheetDialog.setContentText(this.language.get(Language.Key.SelectSheet));

        Optional<String> sheetNameOptional = sheetDialog.showAndWait();
        return sheetNameOptional.map(workbook::getSheet).orElse(null);
    }

    public void openFile(Stage stage) {
        File file = this.chooseOpenFile(stage);
        if (file != null) {
            try (Workbook workbook = WorkbookFactory.create(file)) {
                Sheet sheet = this.chooseSheet(workbook);
                this.tableView.getItems().clear();
                this.tableView.getColumns().clear();
                List<List<String>> excelData = this.loadSheet(sheet);
                excelData.forEach(excelDatum -> this.data.add(FXCollections.observableArrayList(excelDatum)));
                this.tableView.setItems(this.data.stream().skip(1).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                for (int i = 0; i < excelData.get(0).size(); i++) {
                    int currentColumn = i;
                    TableColumn<ObservableList<String>, String> column = new TableColumn<>(this.data.get(0).get(i));
                    column.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(currentColumn)));
                    this.tableView.getColumns().add(column);
                }
            } catch (Exception ignore) {
                this.showAlert(this.language.get(Language.Key.OpenError));
            }
        }
    }

    public void exportFilteredData(Stage stage) {
        File file = this.chooseSaveFile(stage);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet(this.language.get(Language.Key.FilteredDataSheetName));
                Row header = sheet.createRow(0);
                this.tableView.getColumns().forEach(column -> header
                        .createCell(this.tableView.getColumns().indexOf(column))
                        .setCellValue(column.getText()));
                for (int i = 0; i < this.tableView.getItems().size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    ObservableList<String> item = this.tableView.getItems().get(i);
                    this.tableView.getColumns().forEach(col -> {
                        Object cell = col.getCellObservableValue(item).getValue();
                        row.createCell(this.tableView.getColumns().indexOf(col))
                                .setCellValue(cell == null ? "" : cell.toString());
                    });
                }
                workbook.write(new FileOutputStream(file));
            } catch (Exception ignore) {
                this.showAlert(this.language.get(Language.Key.ExportError));
            }
        }
    }

    public void setFilterColumn(int filterIndex, String columnName) {
        int columnIndex = this.getFilterColumnIndex(columnName);
        Filter filter = this.filterValues.get(filterIndex);
        String value = filter == null ? "" : filter.value;
        if (columnIndex >= 0) {
            this.filterValues.set(filterIndex, new Filter(columnIndex, value));
        }
    }

    private int getFilterColumnIndex(String columnName) {
        ObservableList<String> columns = this.data.get(0);
        int columnIndex = -1;
        int i = 0;
        while (columnIndex == -1 && i < columns.size()) {
            if (columns.get(i).equals(columnName)) {
                columnIndex = i;
            }
            i++;
        }
        return columnIndex;
    }

    public void setFilterValue(int filterIndex, String value) {
        this.filterValues.set(filterIndex, new Filter(-1, value));
    }

    public void filter() {
        String value = this.filterValues.get(0).value; // placeholder fixme (and below too)
        if (value.isEmpty()) {
            this.tableView.setItems(this.data.stream().skip(1).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } else {
            this.tableView.setItems(this.data.stream().skip(1).filter(list -> {
                return false;
            }).filter(list -> {
                return false;
            }).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
    }
}
