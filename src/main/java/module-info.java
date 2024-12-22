module org.example.tablemanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.example.tablemanager to javafx.fxml;
    exports org.example.tablemanager;
}