package org.example.tablemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static final Language LANG = Language.en_US;
    private static final String ICON_PATH = "/icon.png";
    private static final String COLOR = "#004490";

    @Override
    public void start(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));

        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.setResizable(true);

        GridPane root = new GridPane();
        root.setMinSize(500, 300);

        Label label = new Label("test");
        root.getChildren().add(label);
        Button exitButton = new Button(LANG.get(Language.Key.Exit));
        root.getChildren().add(exitButton);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}