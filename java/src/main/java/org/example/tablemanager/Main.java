package org.example.tablemanager;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static final Language LANG = Language.en_US;
    private static final String ICON_PATH = "/icon.png";

    @Override
    public void start(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));

        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.setResizable(true);

        Button openItem = new Button(LANG.get(Language.Key.OpenFile));
        openItem.setId("open-item");

        Button exitItem = new Button(LANG.get(Language.Key.Exit));
        exitItem.setId("exit-item");
        exitItem.setOnAction(actionEvent -> System.exit(0));

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(openItem, exitItem);

        HBox root = new HBox();
        root.setMinSize(500, 300);
        root.getChildren().add(toolBar);

        TextField textField = new TextField(LANG.get(Language.Key.Filter));
        root.getChildren().add(textField);
        String s = textField.getText();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}