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
     * tla = si -> Para el condicional "if"
     * neneuhca = goma de mascar -> para el ciclo "for" por la terminología de que lo mascaras hasta que se acabe su sabor, el for acaba hasta que cumpla una condición*/
    public List<String> reservadas = Arrays.asList("neneuhca", "ixquichca", "calpulli", "macehualtin", "ichtaca", "tla", "tlanamo", "nen", "cuepa", "tlapohualli", "tlatolli");
    //lista de operadores
    public List<String> operadores = Arrays.asList("+", "-", "*", "/", "=", "<", ">", "(", ")", "{", "}", ";");

    //este metodo se ejecuta cada que inicia el programa

    /**
     * Método de inicialización del controlador JavaFX.
     * Se ejecuta automáticamente al cargar la pantalla.
     * Realiza dos tareas principales:
     * 1-> Carga las palabras reservadas en una tabla hash con archivo de acceso aleatorio.
     * 2-> Activa el resaltado de sintaxis en tiempo real en el editor de texto.
     */
    @FXML
    private void initialize() {

        //---------------------------------------------
        // parte 2 -> config, del editor de texto

        // La consola no debe ser editable por el usuario, el lado derecho de la interfaz
        txtConsola.setEditable(false);

        // El panel de texto con colores no debe recibir foco ni clics del mouse ya que este es solo decorativo y donde se pueden pintar las palabras
        // ya que es solo visual, el usuario escribe en txtArea
        txtFlow.setFocusTraversable(false);
        txtFlow.setMouseTransparent(true);

        // Listener que se activa cada vez que el usuario escribe en el editor de texto
        txtArea.textProperty().addListener((obs, oldText, newText) -> {

            // Limpia el panel visual por si se quedó algo
            txtFlow.getChildren().clear();

            // Divide el texto por líneas para respetar los saltos de línea (Enter)
            // El -1 asegura que las líneas vacías al final también se incluyan
            String[] lineas = newText.split("\n", -1);

            //ciclo para recorrer linea por linea lo que escribió el usuario
            for (int i = 0; i < lineas.length; i++) {

                // Divide cada línea por espacios para obtener tokens individuales
                String[] tokens = lineas[i].split(" ");

                //recorre el arreglo de tokens recién creado
                for (String token : tokens) {

                    // Ignora espacios vacíos que puedan surgir al dividir
                    if (token.isEmpty()) continue;

                    // Crea un elemento de texto visual con espacio al final
                    Text txt = new Text(token + " ");

                    // Si el token es una palabra reservada, se resalta
                    // con color aguamarina y negritas (como en un IDE)
                    if (reservadas.contains(token)) {
                        txt.setFill(Color.AQUAMARINE);
                        txt.setStyle("-fx-font-weight: bold");
                    } else {
                        // Si no es reservada, se muestra en color blanco normal
                        txt.setFill(Color.WHITE);
                    }

                    // Agrega el texto al panel visual ya que aqui si se pueden pintar palabras, en el textArea no
                    txtFlow.getChildren().add(txt);
                }

                // Agrega salto de línea entre líneas (excepto la última)
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
        analizarSintaxis();
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
        return esValido;
    }

    //Metodo para analizar la sintaxis
    private void analizarSintaxis() {
        //variable auxiliar para moverse por la lista/arreglo de tokens
        int i = 0;
        //Variable boleana para detecetar errores
        boolean hayErrores = false;

        while (i < tokens.size()) {
            String tok = tokens.get(i);

            // -- Declaración: tipo id = valor ;
            if (i + 4 < tokens.size()
                    && Afd_identidicadores(tokens.get(i + 1)) //posicion de la lista/arreglo del token/linea
                    && tokens.get(i + 2).equals("=")
                    && tokens.get(i + 4).equals(";")) {
                /*variable boleana para saber si el valor asignado coincide
                 con el tipo de dato declarado*/
                boolean valorValido = false;

                /*Si la delcaracion es de tipo numerico (tlapohualli)
                el valor debe de er reconocido por el AFD de numeros*/
                if (tok.equals("tlapohualli")) {
                    valorValido = Afd_numeros(tokens.get(i + 3));
                    /*Si es de tipo tlatolli (texto)
                     * debe de ser reconocido por el AFD de identificadores*/
                } else if (tok.equals("tlatolli")) {
                    valorValido = Afd_identidicadores(tokens.get(i + 3));
                }

                /*Si el valor corresponde al tipo declarado entonces es una declaracion valida*/
                if (valorValido) {
                    txtConsola.appendText("Declaración válida: " + tokens.get(i + 1) + "\n");
                    i += 5; //este =+ 5 hace que se salte a la siguiente declaracion de variable
                    /*Si no es reconocido pues marca error*/
                } else {
                    txtConsola.appendText("Error de tipo en declaración: " + tokens.get(i + 1) + "\n");
                    hayErrores = true;
                    i++;
                }


            } else if (tok.equals("tla")) {
                /*verifica la estructura del bloque tla y devuelve el indice despues del cierre*/
                int resultado = verificarBloque(i, "tla");
                /*Si el resultado es mayor que i significa que el bloque
                 * fue reconocido correctamnete*/
                if (resultado > i) {
                    /*Se guarda la posicion donde termino el bloque*/
                    int siguiente = resultado;
                    // verificar si hay mas (tlanamo/else)
                    if (siguiente < tokens.size() && tokens.get(siguiente).equals("tlanamo")) {
                        /*verificar el bloque tlanamo tenga llaves de apertura y cierre*/
                        int res2 = verificarBloqueSimple(siguiente + 1, "tlanamo");
                        //lo mismo que antes, si debuelve mayor a siguiente pues es valido
                        if (res2 > siguiente) {
                            txtConsola.appendText("tla / tlanamo válido\n");
                            i = res2;
                        } else {
                            txtConsola.appendText("Error en bloque tlanamo\n");
                            hayErrores = true;
                            i = siguiente + 1;
                        }
                    } else {
                        txtConsola.appendText("Condicional tla válido\n");
                        i = resultado;
                    }
                } else {
                    txtConsola.appendText("Error en condicional tla\n");
                    hayErrores = true;
                    i++;
                }

                // Ciclo: neneuhca (condicion)
            } else if (tok.equals("neneuhca")) {
                /*verfica la condicion y el bloque */
                int resultado = verificarBloque(i, "neneuhca");
                //lo mismo, si el resultado es mayor a i es valido
                if (resultado > i) {
                    txtConsola.appendText("Ciclo neneuhca válido\n");
                    i = resultado;
                } else {
                    txtConsola.appendText("Error en ciclo neneuhca\n");
                    hayErrores = true;
                    i++;
                }

                // Asignación simple: id = valor ;
            } else if (Afd_identidicadores(tok) && !reservadas.contains(tok)) {
                if (i + 3 < tokens.size()
                        && tokens.get(i + 1).equals("=")
                        && tokens.get(i + 3).equals(";")) {
                    txtConsola.appendText("Asignación válida: " + tok + "\n");
                    /*se salta:
                     * id = valor;
                     * (4 tokens)*/
                    i += 4;
                } else {
                    txtConsola.appendText("Error en asignación: " + tok + "\n");
                    hayErrores = true;
                    i++;
                }

                // Retorno: cuepa valor ;
            } else if (tok.equals("cuepa")) {
                if (i + 2 < tokens.size() && tokens.get(i + 2).equals(";")) {
                    txtConsola.appendText("Retorno válido\n");
                    i += 3;
                } else {
                    txtConsola.appendText("Error en cuepa, falta ';'\n");
                    hayErrores = true;
                    i++;
                }

            } else {
                i++; // token no reconocido como inicio de sentencia, saltar
            }
        }

        if (!hayErrores) {
            txtConsola.appendText("\nSintaxis correcta\n");
        } else {
            txtConsola.appendText("\nSe encontraron errores de sintaxis\n");
        }
    }

    /**
     * Verifica: palabra ( id op valor )
     * Retorna el índice siguiente al bloque si es válido, o el mismo i si no.
     */
    private int verificarBloque(int i, String nombre) {
        //Lista de operadores relacionales validos en las condiciones
        List<String> ops = Arrays.asList("<", ">", "=", "<=", ">=", "!=");
        // Verifica que existan suficnetes tokens para la condicion
        // estructura esperada: (id, operador, valor)
        if (i + 5 >= tokens.size()) return i;
        //verifica el parentesis de apertura
        if (!tokens.get(i + 1).equals("("))
        if (!Afd_identidicadores(tokens.get(i + 2)) && !Afd_numeros(tokens.get(i + 2))) return i; //veridica operando de lado izquierdo (identificador o numero)
        if (!ops.contains(tokens.get(i + 3))) return i; //verificar operador relacional,
        if (!Afd_identidicadores(tokens.get(i + 4)) && !Afd_numeros(tokens.get(i + 4))) return i; // verificar operdor derecho (idenfitifacor o numero)
        //verifica parentesis de cierre
        if (!tokens.get(i + 5).equals(")")) return i;

        // si la condicion es correcta verifica { }
        return verificarBloqueSimple(i + 6, nombre);
    }

    /**
     * Verifica: (contenido interno ya validado recursivamente)
     * Retorna índice tras el cierre '}', o i si falla.
     */
    private int verificarBloqueSimple(int i, String nombre) {
        //verificar llave de apertura
        if (i >= tokens.size() || !tokens.get(i).equals("{")) return i - 1;
        //comtador para controlar bloques anidados
        int profundidad = 1;
        //comeinza despues de la primera llave
        int j = i + 1;
        while (j < tokens.size() && profundidad > 0) {
            //si a]encuetra otra llave de apertura aumenta la profundidad
            if (tokens.get(j).equals("{")) profundidad++;
            //si encuentra una de cierre la profundidad disminuye
            else if (tokens.get(j).equals("}")) profundidad--;
            j++;
        }
        //si la profundidad no regreso a 0 falta llavesn llaves de cierre
        if (profundidad != 0) return i;
        //regresa la posiciond e la ultima llave
        return j;
    }
}
