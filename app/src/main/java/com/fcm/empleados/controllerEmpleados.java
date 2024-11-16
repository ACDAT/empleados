/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fcm.empleados;

/**
 *
 * @author DFran49
 */
import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class controllerEmpleados implements Initializable{
    private Properties properties = new Properties();
    private ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();
    private Empleado empleadoSeleccionado;

    @FXML
    private TableColumn<Empleado, String> cApellidos;

    @FXML
    private TableColumn<Empleado, String> cDepartamento;

    @FXML
    private TableColumn<Empleado, Integer> cID;

    @FXML
    private TableColumn<Empleado, String> cNombre;

    @FXML
    private TableColumn<Empleado, Double> cSueldo;
    
    @FXML
    private TableView<Empleado> tvEmpleados;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtDepartamento;
    
    @FXML
    private Label txtError;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtSueldo;

    @FXML
    void actualizar(ActionEvent event) {
        if (comprobarCampos()) {
            //Tras comprobar si hay campos en blanco lanza una alerta y el error
            alerta();
            txtError.setText("Alguno de los campos está vacío.");
        } else {
            //Se elimina el mensaje de error y se actualiza
            txtError.setText("");
            listaEmpleados.set(listaEmpleados.lastIndexOf(empleadoSeleccionado), new Empleado(empleadoSeleccionado.getId(), txtNombre.getText(),
                txtApellidos.getText(),txtDepartamento.getText(), Double.parseDouble(txtSueldo.getText())));
        }
        
    }

    @FXML
    void borrar(ActionEvent event) {
        if (listaEmpleados.isEmpty()) {
            //Si no hay empleados muestra una alerta y el error
            alerta();
            txtError.setText("No hay empleados para borrar.");
        } else {
            //Se elimina el mensaje de error y se elimina
            txtError.setText("");
            listaEmpleados.remove(listaEmpleados.lastIndexOf(empleadoSeleccionado));
        }
    }

    @FXML
    void expJSON(ActionEvent event) {
        //Se crea un objeto envoltorio para almacenar los empleados antes de pasarlos a json
        EnvoltorioEmpleados empleados = new EnvoltorioEmpleados();
        //Se sacan los empleados de la ObservableList y se añaden al envoltorio
        listaEmpleados.forEach(e -> {
            empleados.getEmpleados().add(e);
        });
        
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("src/main/resources/" + getPropiedad("fichero_json"))) {
            //Se escribe la lista envoltorio en el archivo indicado por el fichero properties
            gson.toJson(empleados, writer);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    void expXML(ActionEvent event) {
        XStream xstream = new XStream();
        //Se crea una lista con los empleados de la ObservableList
        List<Empleado> empleados = new ArrayList<>(listaEmpleados);
        xstream.alias("empleado", Empleado.class);

        try (FileWriter writer = new FileWriter(new File("src/main/resources/" + getPropiedad("fichero_xml")))) {
            //Se crea una linea que contiene el xml a partir de la lista y se escribe en el archivo indicado por el fichero properties
            String xml = xstream.toXML(empleados);
            writer.write(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void impJSON(ActionEvent event) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("src/main/resources/" + getPropiedad("fichero_json"))) {
            //Se crea un objeto envoltorio para almacenar los empleados recuperados de json
            EnvoltorioEmpleados empleados = gson.fromJson(reader, EnvoltorioEmpleados.class);
            //Se vacía la ObservableList de empleados
            listaEmpleados.clear();
            //Se rellena la ObservableList con los empleados leidos a la lista envoltorio
            empleados.getEmpleados().forEach(e -> {
                listaEmpleados.add(e);
            });
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    void impXML(ActionEvent event) {
        XStream xstream = new XStream();
        xstream.alias("empleado", Empleado.class);

        try {
            File file = new File("src/main/resources/" + getPropiedad("fichero_xml"));
            //Se le da permiso de deserialización sobre la clase Empleado
            xstream.addPermission(new ExplicitTypePermission(new Class[]{com.fcm.empleados.Empleado.class}));
            //Se crea la lista empleados al leer del archivo
            List<Empleado> empleados = (List<Empleado>) xstream.fromXML(file);
            //Se vacía la ObservableList y se escriben los empleados leidos
            listaEmpleados.clear();
            for (Empleado empleado : empleados) {
                listaEmpleados.add(empleado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void insertar(ActionEvent event) {
        if (comprobarCampos()) {
            //Tras comprobar si hay campos en blanco lanza una alerta y el error
            alerta();
            txtError.setText("Alguno de los campos está vacío.");
        } else {
            //Se elimina el mensaje de error y se inserta
            txtError.setText("");
            siguienteEmpleado();
            listaEmpleados.add(new Empleado(Integer.parseInt(getPropiedad("id_empleado")), txtNombre.getText(),
                    txtApellidos.getText(),txtDepartamento.getText(), Double.parseDouble(txtSueldo.getText())));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Se asignan las validaciones de campos
        setValidaciones();
        //Se carga el fichero properties
        try (FileInputStream fileIn = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(fileIn);
        } catch (IOException e) {
            System.err.println("Error al cargar las propiedades: " + e.getMessage());
        }
        //Se prepara la tabla para cargar empleados
        prepararTabla();
        //Se cargan empleados desde el binario
        cargar();
    }
    
    private void prepararTabla() {
        //Asigna a cada celda el atributo correspondiente de la clase
        prepararCeldas();
        //Vincula la tabla y la ObservableList
        tvEmpleados.setItems(listaEmpleados);
        //Se prepara un listener para guardar el empleado seleccionado
        tvEmpleados.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                setEmpleado(nuevoValor);
            }
        });
    }
    
    private void prepararCeldas() {
        //Asigna a cada celda el atributo correspondiente de la clase
        cID.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        cDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        cSueldo.setCellValueFactory(new PropertyValueFactory<>("sueldo"));
    }
    
    private void setEmpleado(Object nuevoValor) {
        //Se guarda en variable de clase el empleado seleccionado en la tabla
        empleadoSeleccionado = (Empleado) nuevoValor;
        txtNombre.setText(empleadoSeleccionado.getNombre());
        txtApellidos.setText(empleadoSeleccionado.getApellidos());
        txtDepartamento.setText(empleadoSeleccionado.getDepartamento());
        txtSueldo.setText(String.valueOf(empleadoSeleccionado.getSueldo()));
    }

    private String getPropiedad(String propiedad) {
        //Lee la propiedad pasada por parámetro del fichero properties
        return properties.getProperty(propiedad);
    }
    
    private void siguienteEmpleado() {
        //Aumenta el id de empleado del fichero propperties
        Integer id = Integer.parseInt(properties.getProperty("id_empleado"))+1;
        properties.setProperty("id_empleado", id.toString());
        try (FileOutputStream fileOut = new FileOutputStream("src/main/resources/config.properties")) {
            properties.store(fileOut, "ID empleado");
        } catch (IOException e) {
            System.err.println("Error al guardar las propiedades: " + e.getMessage());
        }
    }
    
    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/main/resources/" + getPropiedad("fichero_binario")))) {
            //Se lee la cantidad de empleados que hay escritos y se añaden a la ObservableList
            int cantidadLeida = ois.readInt();
            for (int i = 0; i < cantidadLeida; i++) {
                listaEmpleados.add((Empleado) ois.readObject());
            }
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(controllerEmpleados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(controllerEmpleados.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void guardar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/" + getPropiedad("fichero_binario"),false))) {
            //Se escribe la cantidad de empleados que hay en la ObservableList y se cargan del archivo
            oos.writeInt(listaEmpleados.size());
            for (int i = 0; i < listaEmpleados.size(); i++) {
                oos.writeObject(listaEmpleados.get(i));
            }
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(controllerEmpleados.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setValidaciones() {
        valNombre();
        valApellidos();
        valDepartamento();
        valSueldo();
        
    }
    
    private void alerta() {
        //Se llama a una alerta genérica
        Alert alertError=new Alert(Alert.AlertType.ERROR);
        alertError.setTitle("Error");
        alertError.setHeaderText("No ha respetado las restricciones a los valores.");
        alertError.setContentText("Mire en el apartado \"Info\" sobre la tabla para más detalles.");
        alertError.showAndWait();
    }
    
    private void valNombre() {
        //Al quitar el foco del campo "Nombre" comprueba si está vacío o si cumple la validación, y si no cumple alguno, 
        //se muestra el error, el campo se pone en blanco y se muestra el error
        txtNombre.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtNombre.getText().matches("([a-zA-ZáéíóúÁÉÍÓÚñÑ]\\s?){1,30}") && !txtNombre.getText().isEmpty()){
                    alerta();
                    txtNombre.setText("");
                    txtError.setText("El nombre debe tener una longitud entre 1 y 30 letras.");
                }
            }
        });
    }
    
    private void valApellidos() {
        //Al quitar el foco del campo "Apellidos" comprueba si está vacío o si cumple la validación, y si no cumple alguno, 
        //se muestra el error, el campo se pone en blanco y se muestra el error
        txtApellidos.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if(!txtApellidos.getText().matches("([a-zA-ZáéíóúÁÉÍÓÚñÑ]\\s?){1,60}") && !txtApellidos.getText().isEmpty()){
                    alerta();
                    txtApellidos.setText("");
                    txtError.setText("El apellido debe tener una longitud entre 1 y 60 letras.");
                }
            }
        });
    }
    
    private void valDepartamento() {
        //Al quitar el foco del campo "Departamento" comprueba si está vacío o si cumple la validación, y si no cumple alguno, 
        //se muestra el error, el campo se pone en blanco y se muestra el error
        txtDepartamento.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if(!txtDepartamento.getText().matches("([a-zA-ZáéíóúÁÉÍÓÚñÑ]\\s?){1,30}") && !txtDepartamento.getText().isEmpty()){
                    alerta();
                    txtDepartamento.setText("");
                    txtError.setText("El departamento debe tener una longitud entre 1 y 30 letras.");
                }
            }
        });
    }
    
    private void valSueldo() {
        //Al quitar el foco del campo "Sueldo" comprueba si está vacío o si cumple la validación, y si no cumple alguno, 
        //se muestra el error, el campo se pone en blanco y se muestra el error
        txtSueldo.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if(!txtSueldo.getText().matches("\\d{1,5}.\\d{1,2}") && !txtSueldo.getText().isEmpty()){
                    alerta();
                    txtSueldo.setText("");
                    txtError.setText("El sueldo debe seguir el siguiente formato: XXXXX.XX.");
                }
            }
        });
    }
    
    private boolean comprobarCampos() {
        //Se comprueba si hay algún campo vacío para no dejar escribir o actualizar
        return txtNombre.getText().isEmpty() || txtApellidos.getText().isEmpty() || txtDepartamento.getText().isEmpty() || txtSueldo.getText().isEmpty();
    }
}
