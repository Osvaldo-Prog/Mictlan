package org.example.mictlan_compilador.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainController {
    @FXML
    private Label lblPrincipal;
    @FXML
    private TextArea txtArea;
    //metodo para cerrar la ventana actual=====================================================================
    public void cerrarVentanaActual(ActionEvent event) {
        // Obtener el stage actual desde el evento
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //Metodo para abrir el explorador de arhivos y cargar el codigo que requiere en txt
    public void cargarArchivo(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo txt");
        //Para darle a entender que solo seleccione archivos txt
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo", "*.txt"));
        File file = fileChooser.showOpenDialog(lblPrincipal.getScene().getWindow());
        if(file != null){
            //Bufferered reader para que lea linea por linea el txt cargado
            try(BufferedReader br = new BufferedReader(new FileReader(file))){
                //StrngBuilder para concatenarle las lineas a un solo string y despues mandarselo al txtArea
                StringBuilder contenidoTxtArea = new StringBuilder();
                String lineaLeida;
                while((lineaLeida = br.readLine()) != null){
                    contenidoTxtArea.append(lineaLeida + "\n");
                }
                txtArea.setText(contenidoTxtArea.toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
