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

public class JavaFXApp extends Application {
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
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Visualizador Excel");

        // Set application icon
        primaryStage.getIcons().add(new Image("file:icon.png"));

        BorderPane root = new BorderPane();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Archivo");
        MenuItem openItem = new MenuItem("Abrir");
        openItem.setOnAction(e -> openFile(primaryStage));
        MenuItem exportItem = new MenuItem("Exportar");
        exportItem.setOnAction(e -> exportFilteredData(primaryStage));
        fileMenu.getItems().addAll(openItem, exportItem);
        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        // Filter and sort controls
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        firstColumnFilter = new ComboBox<>();
        firstColumnFilter.setPromptText("Primera columna");

        firstFilterField = new TextField();
        firstFilterField.setPromptText("Filtro 1");
        firstFilterField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        secondColumnFilter = new ComboBox<>();
        secondColumnFilter.setPromptText("Segunda columna");

        secondFilterField = new TextField();
        secondFilterField.setPromptText("Filtro 2");
        secondFilterField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        sortColumn = new ComboBox<>();
        sortColumn.setPromptText("Ordenar por");

        Button sortButton = new Button("Ordenar");
        sortButton.setOnAction(e -> sortTable());

        controls.getChildren().addAll(firstColumnFilter, firstFilterField, secondColumnFilter, secondFilterField, sortColumn, sortButton);
        root.setCenter(controls);

        // Table view
        tableView = new TableView<>();
        root.setBottom(tableView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione un archivo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xls", "*.xlsx", "*.xlsm"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
                List<String> sheetNames = new ArrayList<>();
                for (Sheet sheet : workbook) {
                    sheetNames.add(sheet.getSheetName());
                }

                ChoiceDialog<String> sheetDialog = new ChoiceDialog<>(sheetNames.get(0), sheetNames);
                sheetDialog.setTitle("Seleccionar Hoja");
                sheetDialog.setHeaderText(null);
                sheetDialog.setContentText("Seleccione una hoja:");

                Optional<String> result = sheetDialog.showAndWait();
                result.ifPresent(sheetName -> loadSheet(workbook.getSheet(sheetName)));

            } catch (IOException e) {
                showAlert("Error", "No se pudo abrir el archivo.");
            }
        }
    }

    private void loadSheet(Sheet sheet) {
        data = new ArrayList<>();
        tableView.getColumns().clear();

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue();
            headers.add(header);
            TableColumn<Map<String, String>, String> column = new TableColumn<>(header);
            column.setCellValueFactory(new PropertyValueFactory<>(header));
            tableView.getColumns().add(column);
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String value = getCellValue(cell);
                rowData.put(headers.get(j), value);
            }
            data.add(rowData);
        }

        tableView.setItems(FXCollections.observableArrayList(data));

        firstColumnFilter.setItems(FXCollections.observableArrayList(headers));
        secondColumnFilter.setItems(FXCollections.observableArrayList(headers));
        sortColumn.setItems(FXCollections.observableArrayList(headers));
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell)
                        ? new SimpleDateFormat("dd-MM-yyyy").format(cell.getDateCellValue())
                        : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private void filterTable() {
        if (data == null) return;

        String filter1 = firstFilterField.getText().toLowerCase();
        String column1 = firstColumnFilter.getValue();

        String filter2 = secondFilterField.getText().toLowerCase();
        String column2 = secondColumnFilter.getValue();

        List<Map<String, String>> filteredData = data.stream().filter(row -> {
            boolean matches = true;
            if (column1 != null && !filter1.isEmpty()) {
                matches &= row.getOrDefault(column1, "").toLowerCase().contains(filter1);
            }
            if (column2 != null && !filter2.isEmpty()) {
                matches &= row.getOrDefault(column2, "").toLowerCase().contains(filter2);
            }
            return matches;
        }).collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filteredData));
    }

    private void sortTable() {
        if (data == null) return;

        String column = sortColumn.getValue();
        if (column == null) return;

        data.sort(Comparator.comparing(row -> row.getOrDefault(column, ""), Comparator.nullsFirst(String::compareTo)));
        if (!sortAscending) Collections.reverse(data);
        sortAscending = !sortAscending;

        tableView.setItems(FXCollections.observableArrayList(data));
    }

    private void exportFilteredData(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Datos Filtrados");

                // Write headers
                Row headerRow = sheet.createRow(0);
                List<String> headers = new ArrayList<>(data.get(0).keySet());
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }

                // Write data
                List<Map<String, String>> tableData = tableView.getItems();
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
                showAlert("Error", "No se pudo exportar el archivo.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
