package org.example.mictlan_compilador.Controller;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Stage;
import org.example.mictlan_compilador.Modelo.Simbolo;

import java.util.List;

public class ControllerTablaSimbolos {
    @FXML private TableView<Simbolo> tablaSimbolos;
    @FXML private TableColumn<Simbolo, Integer> colID;
    @FXML private TableColumn<Simbolo, String> colReservada;


    @FXML
    public void initialize(){
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReservada.setCellValueFactory(new PropertyValueFactory<>("palabra"));
    }

    //metodo para cerrar la ventana actual=====================================================================
    public void cerrarVentanaActual(ActionEvent event) {
        // Obtener el stage actual desde el evento
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setTablaSimbolos(List<Simbolo> lista){
        tablaSimbolos.getItems().clear();
        tablaSimbolos.getItems().addAll(lista);
    }
}
