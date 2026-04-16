module tcrs {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;

    opens tcrs to javafx.fxml;
    exports tcrs;
}
