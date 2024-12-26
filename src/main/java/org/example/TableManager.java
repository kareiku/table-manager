package org.example;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class TableManager extends Application {
    private static final Language LANG = Language.en_US;
    private Controller controller;
    private Stage stage;

    private ComboBox<String> firstFilteringColumn;
    private TextField firstFilteringField;
    private ComboBox<String> secondFilteringColumn;
    private TextField secondFilteringField;
    private TableView<ObservableList<String>> tableView;

    public static void main(String[] args) {
        TableManager.launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.controller = new Controller();
        this.stage = stage;
        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setMaximized(true);
        stage.setResizable(true);

        BorderPane root = new BorderPane();

        MenuItem openItem = new MenuItem(LANG.get(Language.Key.OpenFile));
        openItem.setOnAction(ignored -> this.openFile());

        MenuItem exportItem = new MenuItem(LANG.get(Language.Key.ExportFile));
        exportItem.setOnAction(ignored -> System.console());

        MenuItem exitItem = new MenuItem(LANG.get(Language.Key.Exit));
        exitItem.setOnAction(ignored -> System.exit(0));

        root.setTop(new MenuBar(new Menu(LANG.get(Language.Key.FileMenu), null, openItem, exportItem, exitItem)));

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        this.firstFilteringColumn = new ComboBox<>();
        this.firstFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        this.firstFilteringColumn.setPrefWidth(140);

        this.firstFilteringField = new TextField();
        this.firstFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        this.firstFilteringField.setPrefWidth(150);
        this.firstFilteringField.textProperty().addListener((ignored0, ignored1, ignored2) -> System.nanoTime());

        this.secondFilteringColumn = new ComboBox<>();
        this.secondFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        this.secondFilteringColumn.setPrefWidth(140);

        this.secondFilteringField = new TextField();
        this.secondFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        this.secondFilteringField.setPrefWidth(150);
        this.secondFilteringField.textProperty().addListener((ignored0, ignored1, ignored2) -> System.nanoTime());

        controls.getChildren().addAll(this.firstFilteringColumn, this.firstFilteringField, this.secondFilteringColumn, this.secondFilteringField);
        root.setCenter(controls);

        this.tableView = new TableView<>();
        this.tableView.setPrefHeight(960);
        root.setBottom(this.tableView);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /*
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
            this.tableView.getItems().clear();
            this.tableView.getColumns().clear();
            List<List<String>> excelData = this.controller.loadSheet(sheet);
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            excelData.forEach(excelDatum -> data.add(FXCollections.observableArrayList(excelDatum)));
            this.tableView.setItems(data.stream().skip(1).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            for (int i = 0; i < excelData.get(0).size(); i++) {
                int currentColumn = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(data.get(0).get(i));
                column.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(currentColumn)));
                this.tableView.getColumns().add(column);
            }
        } catch (Exception ignore) {
        }
    }
}
