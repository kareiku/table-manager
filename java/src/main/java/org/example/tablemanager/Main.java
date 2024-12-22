package org.example.tablemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static final String ICON_PATH = "icon.png";
    private static final String COLOR = "#004490";
    private static final Language LANG = Language.es_ES;

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

    private enum Language {
        en_US("Excel Table Manager", "Exit", "Filter by..."),
        es_ES("Gestor de tablas Excel", "Salir", "Filtrar por...");

        private final String[] messages;

        Language(String... messages) {
            this.messages = messages;
        }

        public String get(int index) {
            return this.messages.length > index ? this.messages[index] : "";
        }
    }
}