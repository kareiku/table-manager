package org.example.tablemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static final Language LANG = Language.en_US;
    private static final String ICON_PATH = "icon.png";
    private static final String COLOR = "#004490";

    @Override
    public void start(Stage stage) {
        try {
            GridPane root = new GridPane();

            root.setMinSize(500, 300);
            stage.setTitle(LANG.get(0));
            stage.setMaximized(true);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
            stage.getIcons().add(icon);

            Scene scene = new Scene(root, 320, 240);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            assert false : ex.getMessage();
        }
    }

    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception ex) {
            assert false : ex.getMessage();
        }
    }
}