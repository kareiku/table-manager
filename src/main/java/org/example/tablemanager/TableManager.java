package org.example.tablemanager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TableManager extends Application {
    private static final Language LANG = Language.en_US;
    private Controller controller;
    private Stage stage;

    private List<Map<String, String>> tableData;
    private boolean sortAscending = true;

    public static void main(String[] args) {
        TableManager.launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.controller = new Controller();
        this.stage = stage;

        this.stage.setTitle(LANG.get(Language.Key.Title));
        this.stage.getIcons().add(new Image("file:icon.png"));
        this.stage.setMaximized(true);
        this.stage.setResizable(true);

        BorderPane root = new BorderPane();

        MenuItem openItem = new MenuItem(LANG.get(Language.Key.OpenFile));
        openItem.setOnAction(ignored -> this.openFile());

        MenuItem exportItem = new MenuItem(LANG.get(Language.Key.ExportFile));
        exportItem.setOnAction(ignored -> this.exportFilteredData());

        MenuItem exitItem = new MenuItem(LANG.get(Language.Key.Exit));
        exitItem.setOnAction(ignored -> System.exit(0));

        root.setTop(new MenuBar(new Menu(LANG.get(Language.Key.FileMenu), null, openItem, exportItem, exitItem)));

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        ComboBox<String> firstFilteringColumn = new ComboBox<>();
        firstFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        firstFilteringColumn.setPrefWidth(140);

        TextField firstFilteringField = new TextField();
        firstFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        firstFilteringField.setPrefWidth(150);
        firstFilteringField.textProperty().addListener((ignored0, ignored1, ignored2) -> this.filterTable());

        ComboBox<String> secondFilteringColumn = new ComboBox<>();
        secondFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        secondFilteringColumn.setPrefWidth(140);

        TextField secondFilteringField = new TextField();
        secondFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        secondFilteringField.setPrefWidth(150);
        secondFilteringField.textProperty().addListener((ignored0, ignored1, ignored2) -> this.filterTable());

        ComboBox<String> sortingColumn = new ComboBox<>();
        sortingColumn.setPromptText(LANG.get(Language.Key.SortPlaceholder));
        sortingColumn.setPrefWidth(130);
        sortingColumn.setOnAction(ignored -> this.sortTable());

        controls.getChildren().addAll(firstFilteringColumn, firstFilteringField, secondFilteringColumn, secondFilteringField, sortingColumn);
        root.setCenter(controls);

        TableView<Map<String, String>> tableView = new TableView<>();
        tableView.setPrefHeight(960);
        root.setBottom(tableView);

        Scene scene = new Scene(root, 800, 600);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /*
    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xls", "*.xlsx", "*.xlsm"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Workbook workbook;
                if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xlsm")) {
                    workbook = new XSSFWorkbook(fis);
                } else if (file.getName().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(fis);
                } else {
                    throw new IllegalArgumentException("Unsupported file format");
                }

                List<String> sheetNames = new ArrayList<>();
                for (Sheet sheet : workbook) {
                    sheetNames.add(sheet.getSheetName());
                }

                ChoiceDialog<String> sheetDialog = new ChoiceDialog<>(sheetNames.get(0), sheetNames);
                sheetDialog.setTitle(LANG.get(Language.Key.Title));
                sheetDialog.setHeaderText(null);
                sheetDialog.setContentText(LANG.get(Language.Key.SelectSheet));

                Optional<String> sheet = sheetDialog.showAndWait();
                sheet.ifPresent(sheetName -> this.loadSheet(workbook.getSheet(sheetName)));

            } catch (IOException | IllegalArgumentException ex) {
                this.showAlert(LANG.get(Language.Key.OpenError));
            }
        }
    }

    private void loadSheet(Sheet sheet) {
        this.tableData = new ArrayList<>();
        this.tableView.getColumns().clear();

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            String header = cell != null ? cell.getStringCellValue() : "";
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
                this.tableData.add(rowData);
            }
        }

        this.tableView.setItems(FXCollections.observableArrayList(this.tableData));

        this.firstFilteringColumn.setItems(FXCollections.observableArrayList(headers));
        this.secondFilteringColumn.setItems(FXCollections.observableArrayList(headers));
        this.sortingColumn.setItems(FXCollections.observableArrayList(headers));
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
        if (this.tableData == null) return;

        String filter1 = this.firstFilteringField.getText().toLowerCase();
        String column1 = this.firstFilteringColumn.getValue();

        String filter2 = this.secondFilteringField.getText().toLowerCase();
        String column2 = this.secondFilteringColumn.getValue();

        List<Map<String, String>> filteredData = this.tableData.stream().filter(row -> {
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
        if (this.tableData == null) return;

        String column = this.sortingColumn.getValue();
        if (column == null) return;

        this.tableData.sort(Comparator.comparing(row -> row.getOrDefault(column, ""), Comparator.nullsLast(String::compareTo)));
        if (!this.sortAscending) Collections.reverse(this.tableData);
        this.sortAscending = !this.sortAscending;

        this.tableView.setItems(FXCollections.observableArrayList(this.tableData));
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
                List<String> headers = new ArrayList<>(this.tableData.get(0).keySet());
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
            } catch (IOException ex) {
                this.showAlert(LANG.get(Language.Key.ExportError));
            }
        }
    }
    */

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LANG.get(Language.Key.Title));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private File chooseOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xls", "*.xlsx", "*.xlsm"));
        return fileChooser.showOpenDialog(this.stage);
    }

    private File chooseSaveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LANG.get(Language.Key.SelectFile));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LANG.get(Language.Key.ExcelFiles), "*.xlsx"));
        return fileChooser.showSaveDialog(this.stage);
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

    private void openFile() {
        File file = this.chooseOpenFile();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = this.chooseSheet(workbook);
            this.controller.loadSheet(sheet);
        } catch (Exception ignore) {
        }
    }
}
