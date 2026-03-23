module org.example.mictlan_compilador {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    opens org.example.mictlan_compilador to javafx.fxml;
    exports org.example.mictlan_compilador;
    exports org.example.mictlan_compilador.Controller;
    opens org.example.mictlan_compilador.Controller to javafx.fxml;
}