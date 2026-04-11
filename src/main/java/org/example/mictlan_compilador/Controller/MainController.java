package org.example.mictlan_compilador.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.mictlan_compilador.Modelo.Simbolo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {
    @FXML
    private Label lblPrincipal;
    @FXML
    private TextArea txtArea;
    @FXML
    private TextArea txtConsola;
    @FXML
    private TextFlow txtFlow;
    /*
    lista para la tbala de simbolos
     */
    public List<Simbolo> tablaSimbolos = new ArrayList<>();

    private List<String> tokens = new ArrayList<>();
    /*Contexto de las palabras en nahuatl y su signfificado
     * quema = si -> Para el condicional "if"
     * tzictli = goma de mascar -> para el ciclo "for" por la terminología de que lo mascaras hasta que se acabe su sabor, el for acaba hasta que cumpla una condición*/
    public List<String> reservadas = Arrays.asList("tzictli", "while", "do", "try", "catch", "return", "quema", "else");
    //lista de operadores
    public List<String> operadores = Arrays.asList("+", "-", "*", "/", "=", "<", ">", "(", ")", "{", "}", ";");

    //este metodo se ejecuta cada que inicia el programa
    @FXML
    private void initialize() {
        //Para agregar las palabras en el randomfile
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("Tabla de simbolos.dat", "rw");
            if (randomAccessFile.length() == 0) {//Si esta vacio escribirá
                List<String> reservadas = Arrays.asList("tzictli", "while", "do", "try", "catch", "return", "quema", "if", "else");
                for (String item : reservadas) {
                    randomAccessFile.writeUTF(item);
                }
            }
            randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        txtConsola.setEditable(false);
        txtFlow.setFocusTraversable(false);
        txtFlow.setMouseTransparent(true);

        txtArea.textProperty().addListener((obs, oldText, newText) -> {
            txtFlow.getChildren().clear();

            // Dividimos por líneas para respetar los enters
            String[] lineas = newText.split("\n", -1); // -1 para incluir líneas vacías al final

            for (int i = 0; i < lineas.length; i++) {
                String[] tokens = lineas[i].split(" "); // separa por espacios dentro de la línea

                for (String token : tokens) {
                    if (token.isEmpty()) continue; // evita tokens vacíos
                    Text txt = new Text(token + " "); // agregamos espacio entre palabras
                    if (reservadas.contains(token)) {
                        txt.setFill(Color.AQUAMARINE);
                        txt.setStyle("-fx-font-weight: bold");
                    } else {
                        txt.setFill(Color.WHITE);
                    }
                    txtFlow.getChildren().add(txt);
                }

                // Agregamos salto de línea después de cada línea
                if (i < lineas.length - 1) {
                    txtFlow.getChildren().add(new Text("\n"));
                }
            }
        });
    }

    //metodo para cerrar la ventana actual=====================================================================
    public void cerrarVentanaActual(ActionEvent event) {
        // Obtener el stage actual desde el evento
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //Metodo para abrir el explorador de arhivos y cargar el codigo que requiere en txt
    public void cargarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo txt");
        //Para darle a entender que solo seleccione archivos txt
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo", "*.txt"));
        File file = fileChooser.showOpenDialog(lblPrincipal.getScene().getWindow());
        if (file != null) {
            //Bufferered reader para que lea linea por linea el txt cargado
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                //StrngBuilder para concatenarle las lineas a un solo string y despues mandarselo al txtArea
                StringBuilder contenidoTxtArea = new StringBuilder();
                String lineaLeida;
                while ((lineaLeida = br.readLine()) != null) {
                    contenidoTxtArea.append(lineaLeida + "\n");
                }
                txtArea.setText(contenidoTxtArea.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo de tokenizador =============================================================
    @FXML
    private void tokenizar() {
        String codigo = txtArea.getText();
        String actual = "";
        tokens.clear();

        for (int i = 0; i < codigo.length(); i++) {
            //Se recorre el caracter del string
            //Ejemplo "hola"
            //        [0,1,2,3] siendo h=0, o=1, l=2, a=3
            char letra = codigo.charAt(i);

            if (Character.isLetterOrDigit(letra) || letra == '.') {
                actual += letra;
            } else {
                if (!actual.isEmpty()) {
                    tokens.add(actual);
                    actual = "";
                }
                //isWhitespace solo es para detectar espacios, tabulaciones o asi
                if (!Character.isWhitespace(letra)) {
                    tokens.add(String.valueOf(letra));
                }
            }
        }
        if (!actual.isEmpty()) {
            tokens.add(actual);
        }

        txtConsola.clear();
        txtConsola.setText(tokens.toString());
    }

    /// /////////////////


    @FXML
    private void mostrarTabla() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/TablaSimbolos.fxml"));
            Parent root = fxmlLoader.load();
            //obtener el controlador de la clase de la tabla de simbolos
            ControllerTablaSimbolos controllerTablaSimbolos = fxmlLoader.getController();
            //llenar la tabla con la lista de esta clase
            controllerTablaSimbolos.setTablaSimbolos(tablaSimbolos);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Tabla de simbolos");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /// //////////////////

    @FXML
    private void Compilar() {
        tokenizar();
        analizarTokens();
    }

    ///  //////////////////////

    private void analizarTokens() {

        tablaSimbolos.clear();
        txtConsola.clear();
        int id = 1;


        // agregar palabras reservadas
        for (String reservada : reservadas) {
            tablaSimbolos.add(new Simbolo(id++, reservada));
        }

        //Para las reservadas
        for (int i = 0; i < tokens.size(); i++) {

            String token = tokens.get(i);
            if (reservadas.contains(token)) {

                String res = token + " -- RESERVADA";
                txtConsola.appendText(res + "\n");

                boolean existe = false;
                for (Simbolo s : tablaSimbolos) {
                    if (s.getPalabra().equals(token)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    tablaSimbolos.add(new Simbolo(id++, token));
                }
            }

            // validar identificadores
            if (Afd_identidicadores(token)) {

                String res = token + " -- IDENTIFICADOR";
                txtConsola.appendText(res + "\n");

                boolean existe = false;

                for (Simbolo s : tablaSimbolos) {
                    if (s.getPalabra().equals(token)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    tablaSimbolos.add(new Simbolo(id++, token));
                }

                // validar numeros
            } else if (Afd_numeros(token)) {

                String res = token + " -- NUMERO";
                txtConsola.appendText(res + "\n");

                boolean existe = false;

                for (Simbolo s : tablaSimbolos) {
                    if (s.getPalabra().equals(token)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    tablaSimbolos.add(new Simbolo(id++, token));
                }

                // validar operadores
            } else if (operadores.contains(token)) {

                String res = token + " -- OPERADOR";
                txtConsola.appendText(res + "\n");

                boolean existe = false;

                for (Simbolo s : tablaSimbolos) {
                    if (s.getPalabra().equals(token)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    tablaSimbolos.add(new Simbolo(id++, token));
                }
            } else {

                txtConsola.appendText(token + " -- ERROR\n");
            }
        }
    }

    /// /////////////////////////////

    private boolean Afd_identidicadores(String token) {

        // for (int i=0; i<tokens.size(); i++){
        boolean esValido = true;
        // String token = tokens.get(i);
        for (int j = 0; j < token.length(); j++) {
            char c = token.charAt(j);
            if (j == 0) {
                if (Character.isLetter(c) || c == '$' || c == '_') {
                    System.out.println(" identificadores pasa a q1");

                } else {
                    System.out.println(" identificadores pasa a q2, error");
                    esValido = false;
                    break;
                }
            } else {
                if (Character.isLetter(c) || c == '$' || c == '_' || Character.isDigit(c)) {
                    System.out.println(" identificadores pasa a q1");

                } else {
                    System.out.println(" identificadores pasa a q2, error");
                    esValido = false;
                    break;
                }
            }

        }
            /*if (esValido){
                System.out.println(token + "IDENTIFICADOR");
                tablaSimbolos.add(token + "IDENTIFICADOR");
            }else{
                System.out.println(token + "ERROR");
            }*/
        //}
        return esValido;
    }

    public boolean Afd_numeros(String token) {
        //for (int i=0; i<tokens.size(); i++) {
        boolean tienePunto = false;
        boolean esValido = true;
        // String token = tokens.get(i);
        for (int j = 0; j < token.length(); j++) {
            char c = token.charAt(j);
            if (j == 0) {
                if (Character.isDigit(c)) {
                    System.out.println("numero pasa a q1 correcto");
                } else {
                    System.out.println("numero pasa a q3 error");
                    esValido = false;
                    break;
                }
            } else {
                if (Character.isDigit(c)) {
                    System.out.println("numero se queda en q1 correcto");
                } else if (c == '.') {
                    if (tienePunto == false) {
                        tienePunto = true;
                        System.out.println("numero se pasa a q2 correcto");
                    } else {
                        System.out.println("numero eeror hay dos puntos");
                        esValido = false;
                        break;
                    }

                } else {
                    System.out.println("numero se pasa a q3 error");
                    esValido = false;
                    break;
                }
            }
        }
        if (tienePunto && token.charAt(token.length() - 1) == '.') {
            esValido = false;
        }
          /*  if (esValido){
                System.out.println(token + "NUMERO");
                tablaSimbolos.add(token + "NUMERO");
            }else{
                System.out.println(token + "ERROR NO ES NUMERO");
            }
        //}*/
        return esValido;
    }


}
