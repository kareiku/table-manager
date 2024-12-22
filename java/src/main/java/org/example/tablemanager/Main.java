package org.example.tablemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private static final String ICON = new File("").getAbsolutePath() + "/src/main/resources/icon.ico";
    private static final String COLOR = "#004490";

    @Override
    public void start(Stage stage) throws IOException {
        GridPane root = new GridPane();

        Label first_column_var = new Label();
        Label second_column_var = new Label();
        Label third_column_var = new Label();

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}