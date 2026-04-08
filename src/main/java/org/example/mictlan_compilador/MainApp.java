package org.example.mictlan_compilador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventListener;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/Views/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Mictlán");
        stage.setScene(scene);
        stage.show();
    }
}
