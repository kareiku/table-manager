package org.example;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public final class TableManager extends Application {
    private static final Language LANG = Language.en_US;

    public static void main(String[] args) {
        TableManager.launch(args);
    }

    @Override
    public void start(Stage stage) {
        record Filter(int columnIndex, TextField filterField) {
        }

        TableView<ObservableList<String>> tableView = new TableView<>();
        Controller controller = new Controller(LANG, tableView);
        List<Filter> filters = new ArrayList<>();

        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setMaximized(true);
        stage.setResizable(true);

        ComboBox<String> firstFilteringColumn = new ComboBox<>();
        ComboBox<String> secondFilteringColumn = new ComboBox<>();
        TextField firstFilteringField = new TextField();
        TextField secondFilteringField = new TextField();

        MenuItem openItem = new MenuItem(LANG.get(Language.Key.OpenFile));
        MenuItem exportItem = new MenuItem(LANG.get(Language.Key.ExportFile));
        MenuItem createFilterItem = new MenuItem(LANG.get(Language.Key.NewFilter)); createFilterItem.setDisable(true);
        MenuItem exitItem = new MenuItem(LANG.get(Language.Key.Exit));

        openItem.setOnAction(ignored -> controller.openFile(stage));
        exportItem.setOnAction(ignored -> controller.exportFilteredData(stage));
//        createFilterItem.setOnAction(ignored -> {});
        exitItem.setOnAction(ignored -> System.exit(0));

        BorderPane root = new BorderPane();

        root.setTop(new MenuBar(new Menu(LANG.get(Language.Key.FileMenu), null, openItem, exportItem, new SeparatorMenuItem(), createFilterItem, new SeparatorMenuItem(), exitItem)));

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        firstFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        firstFilteringColumn.setPrefWidth(140);
        firstFilteringColumn.getSelectionModel().selectedItemProperty().addListener((ignoreObservable, ignoreOldValue, newValue) -> {
            if (newValue != null) {
                controller.setFilterColumn(0, newValue);
            }
        });

        firstFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        firstFilteringField.setPrefWidth(150);
        firstFilteringField.textProperty().addListener((ignoreObservable, ignoreOldValue, newValue) -> {
            controller.setFilterValue(0, newValue);
            controller.filter();
        });

        secondFilteringColumn.setPromptText(LANG.get(Language.Key.ColumnSelectPlaceholder));
        secondFilteringColumn.setPrefWidth(140);
        secondFilteringColumn.getSelectionModel().selectedItemProperty().addListener((ignoreObserver, ignoreOldValue, newValue) -> {
            if (newValue != null) {
                controller.setFilterColumn(1, newValue);
            }
        });

        secondFilteringField.setPromptText(LANG.get(Language.Key.FilterPlaceholder));
        secondFilteringField.setPrefWidth(150);
        secondFilteringField.textProperty().addListener((ignoreObservable, ignoreOldValue, newValue) -> {
            controller.setFilterValue(1, newValue);
            controller.filter();
        });

        controls.getChildren().addAll(firstFilteringColumn, firstFilteringField, secondFilteringColumn, secondFilteringField);
        root.setCenter(controls);

        tableView.setPrefHeight(960);
        root.setBottom(tableView);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
