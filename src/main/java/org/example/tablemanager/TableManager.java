package org.example.tablemanager;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TableManager extends Application {
    private static final Language LANG = Language.en_US;
    private TableView<Map<String, String>> tableView;
    private ComboBox<String> firstColumnFilter;
    private ComboBox<String> secondColumnFilter;
    private TextField firstFilterField;
    private TextField secondFilterField;
    private ComboBox<String> sortColumn;
    private List<Map<String, String>> data;
    private boolean sortAscending = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setMaximized(true);
        stage.setResizable(true);

        BorderPane root = new BorderPane();

        MenuItem openItem = new MenuItem(LANG.get(Language.Key.OpenFile));
        openItem.setOnAction(ignored -> this.openFile(stage));

        MenuItem exportItem = new MenuItem(LANG.get(Language.Key.ExportFile));
        exportItem.setOnAction(ignored -> this.exportFilteredData(stage));

        MenuItem exitItem = new MenuItem(LANG.get(Language.Key.Exit));
        exitItem.setOnAction(ignored -> System.exit(0));

        Menu fileMenu = new Menu(LANG.get(Language.Key.FileMenu));
        fileMenu.getItems().addAll(openItem, exportItem, exitItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        this.firstColumnFilter = new ComboBox<>();
        this.firstColumnFilter.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        this.firstColumnFilter.setMaxWidth(140);

        this.firstFilterField = new TextField();
        this.firstFilterField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        this.firstFilterField.setMaxWidth(150);
        this.firstFilterField.textProperty().addListener((ignored0, ignored1, ignored2) -> this.filterTable());

        this.secondColumnFilter = new ComboBox<>();
        this.secondColumnFilter.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        this.secondColumnFilter.setMaxWidth(140);

        this.secondFilterField = new TextField();
        this.secondFilterField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        this.secondFilterField.setMaxWidth(150);
        this.secondFilterField.textProperty().addListener((ignored0, ignored1, ignored2) -> this.filterTable());

        this.sortColumn = new ComboBox<>();
        this.sortColumn.setPromptText(LANG.get(Language.Key.SortPlaceholder));
        this.sortColumn.setMaxWidth(130);
        this.sortColumn.setOnAction(ignored -> this.sortTable());

        controls.getChildren().addAll(this.firstColumnFilter, this.firstFilterField, this.secondColumnFilter, this.secondFilterField, this.sortColumn);
        root.setCenter(controls);

        this.tableView = new TableView<>();
        this.tableView.setPrefHeight(960);
        root.setBottom(this.tableView);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xls", "*.xlsx", "*.xlsm"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
                List<String> sheetNames = new ArrayList<>();
                for (Sheet sheet : workbook) {
                    sheetNames.add(sheet.getSheetName());
                }

                ChoiceDialog<String> sheetDialog = new ChoiceDialog<>(sheetNames.get(0), sheetNames);
                sheetDialog.setTitle(LANG.get(Language.Key.Title));
                sheetDialog.setHeaderText(null);
                sheetDialog.setContentText(LANG.get(Language.Key.SelectSheet));

                Optional<String> result = sheetDialog.showAndWait();
                result.ifPresent(sheetName -> this.loadSheet(workbook.getSheet(sheetName)));
            } catch (IOException e) {
                this.showAlert(LANG.get(Language.Key.OpenError));
            }
        }
    }

    private void loadSheet(Sheet sheet) {
        this.data = new ArrayList<>();
        this.tableView.getColumns().clear();

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue();
            headers.add(header);
            TableColumn<Map<String, String>, String> column = new TableColumn<>(header);
            column.setCellValueFactory(new PropertyValueFactory<>(header));
            this.tableView.getColumns().add(column);
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = this.getCellValue(cell);
                    rowData.put(headers.get(j), value);
                }
                this.data.add(rowData);
            }
        }

        this.tableView.setItems(FXCollections.observableArrayList(this.data));

        this.firstColumnFilter.setItems(FXCollections.observableArrayList(headers));
        this.secondColumnFilter.setItems(FXCollections.observableArrayList(headers));
        this.sortColumn.setItems(FXCollections.observableArrayList(headers));
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? new SimpleDateFormat("dd-MM-yyyy").format(cell.getDateCellValue())
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private void filterTable() {
        if (this.data == null) return;

        String filter1 = this.firstFilterField.getText().toLowerCase();
        String column1 = this.firstColumnFilter.getValue();

        String filter2 = this.secondFilterField.getText().toLowerCase();
        String column2 = this.secondColumnFilter.getValue();

        List<Map<String, String>> filteredData = this.data.stream().filter(row -> {
            boolean matches = true;
            if (column1 != null && !filter1.isEmpty()) {
                matches &= row.getOrDefault(column1, "").toLowerCase().contains(filter1);
            }
            if (column2 != null && !filter2.isEmpty()) {
                matches &= row.getOrDefault(column2, "").toLowerCase().contains(filter2);
            }
            return matches;
        }).collect(Collectors.toList());

        this.tableView.setItems(FXCollections.observableArrayList(filteredData));
    }

    private void sortTable() {
        if (this.data == null) return;

        String column = this.sortColumn.getValue();
        if (column == null) return;

        this.data.sort(Comparator.comparing(row -> row.getOrDefault(column, ""), Comparator.nullsLast(String::compareTo)));
        if (!this.sortAscending) Collections.reverse(this.data);
        this.sortAscending = !this.sortAscending;

        this.tableView.setItems(FXCollections.observableArrayList(this.data));
    }

    private void exportFilteredData(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SaveFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xlsx"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet(LANG.get(Language.Key.FilteredDataSheetName));

                Row headerRow = sheet.createRow(0);
                List<String> headers = new ArrayList<>(this.data.get(0).keySet());
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }

                List<Map<String, String>> tableData = this.tableView.getItems();
                for (int i = 0; i < tableData.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Map<String, String> rowData = tableData.get(i);
                    for (int j = 0; j < headers.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(headers.get(j)));
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            } catch (IOException e) {
                this.showAlert(LANG.get(Language.Key.ExportError));
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LANG.get(Language.Key.Title));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
