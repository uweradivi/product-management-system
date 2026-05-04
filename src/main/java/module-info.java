module com.store {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.base;

    opens com.store.ui         to javafx.fxml, javafx.graphics;
    opens com.store.controller to javafx.fxml;
    opens com.store.component  to javafx.base;
    opens com.store.util       to javafx.base;
    opens com.store.auth       to javafx.base;

    exports com.store.ui;
    exports com.store.controller;
    exports com.store.component;
    exports com.store.util;
    exports com.store.auth;
}
