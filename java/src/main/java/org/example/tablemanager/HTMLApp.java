package org.example.tablemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;

public class HTMLApp extends Application {
    private static final Language LANG = Language.en_US;
    private static final String ICON_PATH = "/icon.png";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));

        stage.setTitle(LANG.get(Language.Key.Title));
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.setResizable(true);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("/index.html")).toExternalForm());

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}