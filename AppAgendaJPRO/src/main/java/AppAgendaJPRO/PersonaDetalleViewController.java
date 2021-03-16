/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppAgendaJPRO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import AppAgendaJPRO.Persona;
import AppAgendaJPRO.Provincia;

/**
 * FXML Controller class
 *
 * @author Eduardo
 */
public class PersonaDetalleViewController implements Initializable 
{
    @FXML
    private AnchorPane rootPersonaDetalleView;    
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private Button buttonGuardar;
    @FXML
    private Button buttonCancelar;
        
    private Pane rootAgendaView;
    
    private TableView tableViewPrevio; 
    private Persona persona; 
    private EntityManager entityManager; 
    private boolean nuevaPersona;
    
    public static final char CASADO = 'C'; 
    public static final char SOLTERO = 'S'; 
    public static final char VIUDO = 'V';
    
    public static final String CARPETA_FOTOS = "src/appagenda/Fotos";
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private Button ButtonSuprimirFoto;
    
    
    public void setPersona(EntityManager entityManager, Persona persona, Boolean nuevaPersona)
    {
        this.entityManager = entityManager;    
        entityManager.getTransaction().begin(); 
        
        if (!nuevaPersona)
        { 
            this.persona = entityManager.find(Persona.class, persona.getId());
        } 
        else 
        { 
            this.persona = persona;
        }
        
        this.nuevaPersona = nuevaPersona;
    }
    
    public void setRootAgendaView(Pane rootAgendaView)
    { 
        this.rootAgendaView = rootAgendaView; 
    }
    
    
    public void setTableViewPrevio(TableView tableViewPrevio)
    { 
        this.tableViewPrevio=tableViewPrevio; 
    }
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    

    
    @FXML
    private void onActionButtonGuardar(ActionEvent event) 
    {
        boolean errorFormato = false;
        
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot(); 
        rootMain.getChildren().remove(rootPersonaDetalleView); 
        rootAgendaView.setVisible(true);
        
        persona.setNombre(textFieldNombre.getText()); 
        persona.setApellidos(textFieldApellidos.getText()); 
        persona.setTelefono(textFieldTelefono.getText()); 
        persona.setEmail(textFieldEmail.getText()); 
        
                
        if (!textFieldNumHijos.getText().isEmpty())
        { 
            try 
            { 
                persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText())); 
            } 
            catch(NumberFormatException ex)
            { 
                errorFormato = true; 
                Alert alert = new Alert(AlertType.INFORMATION, "Numero de hijos no valido"); 
                alert.showAndWait(); 
                textFieldNumHijos.requestFocus(); 
            } 
        }
        
        
        if (!textFieldSalario.getText().isEmpty())
        { 
            try 
            { 
                persona.setSalario(BigDecimal.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue())); 
            } 
            catch(NumberFormatException ex) 
            { 
                errorFormato = true; 
                Alert alert = new Alert(AlertType.INFORMATION, "Salario no valido"); 
                alert.showAndWait(); 
                textFieldSalario.requestFocus(); 
            } 
        }
        
        
        persona.setJubilado(checkBoxJubilado.isSelected());
        
        
        if (radioButtonCasado.isSelected())        
            persona.setEstadoCivil(CASADO);
        
        else if (radioButtonSoltero.isSelected())
            persona.setEstadoCivil(SOLTERO);
        
        else if (radioButtonViudo.isSelected())                        
            persona.setEstadoCivil(VIUDO); 
        
        
        if (datePickerFechaNacimiento.getValue() != null)
        { 
            LocalDate localDate = datePickerFechaNacimiento.getValue(); 
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault()); 
            Instant instant = zonedDateTime.toInstant(); 
            Date date = Date.from(instant); 
            persona.setFechaNacimiento(date); 
        } 
        else         
            persona.setFechaNacimiento(null);         
        
        
        if (comboBoxProvincia.getValue() != null)
        { 
            persona.setProvincia(comboBoxProvincia.getValue()); 
        } 
        else 
        { 
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una provincia"); 
            alert.showAndWait(); 
            errorFormato = true; 
        }
        
                                                 
        if(!errorFormato)
        {
            try
            {
                // Los datos introducidos son correctos
                if (nuevaPersona)       
                    entityManager.persist(persona); 

                else        
                    entityManager.merge(persona);

                entityManager.getTransaction().commit();
        
            }
            catch (RollbackException ex)
            {
                // Los datos introducidos no cumplen requisitos de BD
                Alert alert = new Alert(AlertType.INFORMATION); 
                alert.setHeaderText("No se han podido guardar los cambios. " + "Compruebe que los datos cumplen los requisitos"); 
                alert.setContentText(ex.getLocalizedMessage()); 
                alert.showAndWait();
            }
        }
        
        // Actualiza los nuevos datos en el TableView. Si se trata de una nueva persona, selecciona la ultima fila.
        // Si la persona ya existr se volvers a seleccionar la linea en la que se encontraba.
        int numFilaSeleccionada; 
        
        if (nuevaPersona)
        { 
            tableViewPrevio.getItems().add(persona); 
            numFilaSeleccionada = tableViewPrevio.getItems().size()- 1; 
            tableViewPrevio.getSelectionModel().select(numFilaSeleccionada); 
            tableViewPrevio.scrollTo(numFilaSeleccionada); 
        } 
        else 
        { 
            numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex(); 
            tableViewPrevio.getItems().set(numFilaSeleccionada,persona); 
        } 
        
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada,null); 
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        
        
    }

    
    @FXML
    private void onActionButtonCancelar(ActionEvent event) 
    {
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot(); 
        rootMain.getChildren().remove(rootPersonaDetalleView); 
        rootAgendaView.setVisible(true);
        
        // Anula la transaccion que se habia empezado en el codigo del metodo setPersona
        // Y devuelve el foco al TableView
        entityManager.getTransaction().rollback(); 
        
        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex(); 
        
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null); 
        tableViewPrevio.getFocusModel().focus(pos); 
        tableViewPrevio.requestFocus();        
    }
    
                    
    public void mostrarDatos()
    {
        textFieldNombre.setText(persona.getNombre());           // Nombre de la persona
        textFieldApellidos.setText(persona.getApellidos());     // Apellidos de la persona
        textFieldTelefono.setText(persona.getTelefono());       // Numero de telefono de la persona
        textFieldEmail.setText(persona.getEmail());             // eMail de la persona
        
        
        // Numero de Hijos de la persona
        if (persona.getNumHijos() != null)
        { 
            textFieldNumHijos.setText(persona.getNumHijos().toString()); 
        }
        
        
        // Salario de la persona
        if (persona.getSalario() != null)
        { 
            textFieldSalario.setText(persona.getSalario().toString()); 
        }
        
        
        // ¿Esta Jubilada?
        if (persona.getJubilado() != null)
        { 
            checkBoxJubilado.setSelected(persona.getJubilado()); 
        }
        
        
        // Estado Civil de la persona
        if (persona.getEstadoCivil() != null)
        { 
            switch(persona.getEstadoCivil())
            { 
                case CASADO:
                    radioButtonCasado.setSelected(true); 
                    break; 
                    
                case SOLTERO: 
                    radioButtonSoltero.setSelected(true); 
                    break; 
                
                case VIUDO: 
                    radioButtonViudo.setSelected(true); 
                break; 
            } 
        }
        
        
        // Fecha de Nacimiento de la persona
        if (persona.getFechaNacimiento() != null)
        { 
            Date date = persona.getFechaNacimiento(); 
            Instant instant = date.toInstant(); 
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault()); 
            LocalDate localDate = zdt.toLocalDate(); 
            datePickerFechaNacimiento.setValue(localDate); 
        }
        
        
        // Obtiene todos los registros de la tabla Provincia y los asigna a la lista despegable
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Provincia.findAll"); 
        List listProvincia = queryProvinciaFindAll.getResultList(); 
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        
        // Si el objeto Persona tiene asignada una Provincia, esta se mostrara directamente en el ComboBox
        if (persona.getProvincia() != null)        
            comboBoxProvincia.setValue(persona.getProvincia());         
        
        // Mostrara en la lista de objetos del ComboBox el codigo y el nombre de la provincia                
        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){ 
            @Override 
            public String toString(Provincia provincia)
            { 
                if (provincia == null)
                { 
                    return null;
                }
                else 
                { 
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }
            
            @Override 
            public Provincia fromString(String userId)
            { 
                return null; 
            } 
        });
         
        
        // Imagen
        if (persona.getFoto() != null)
        { 
            String imageFileName = persona.getFoto(); 
            File file = new File(CARPETA_FOTOS + "/" + imageFileName); 
            
            if (file.exists())
            { 
                Image image = new Image(file.toURI().toString()); 
                imageViewFoto.setImage(image); 
            } 
            else 
            { 
                Alert alert = new Alert(AlertType.INFORMATION, "No se encuentra la imagen en " + file.toURI().toString()); 
                alert.showAndWait(); 
            } 
        }                        
                
    }

    
    @FXML
    private void OnActionButtonExaminar(ActionEvent event) 
    {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists())
        { 
            carpetaFotos.mkdir(); 
        } 
        FileChooser fileChooser = new FileChooser(); 
        fileChooser.setTitle("Seleccionar imagen"); 
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Imagenes (jpg, png)", "*.jpg", "*.png"), new FileChooser.ExtensionFilter("Todos los archivos","*.*")); 
        File file = fileChooser.showOpenDialog( rootPersonaDetalleView.getScene().getWindow());
        if (file != null)
        { 
            try 
            {
                Files.copy(file.toPath(), new File(CARPETA_FOTOS + "/" + file.getName()).toPath()); 
                persona.setFoto(file.getName()); 
                Image image = new Image(file.toURI().toString()); 
                imageViewFoto.setImage(image);
            }
            catch (FileAlreadyExistsException ex)
            { 
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado"); 
                alert.showAndWait(); 
            }
            catch (IOException ex)
            { 
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen"); 
                alert.showAndWait(); 
            }
        }
    }

    @FXML
    private void OnActionButtonSuprimir(ActionEvent event) 
    {
        Alert alert = new Alert(AlertType.CONFIRMATION); 
        alert.setTitle("Confirmar supresion de imagen"); 
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n" + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operacion?"); 
        alert.setContentText("Elija la opcion deseada:"); 
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir"); 
        ButtonType buttonTypeMantener = new ButtonType("Mantener"); 
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE); 
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel); 
        Optional<ButtonType> result = alert.showAndWait(); 
        
        if (result.get() == buttonTypeEliminar)
        { 
            String imageFileName = persona.getFoto(); 
            File file = new File(CARPETA_FOTOS + "/" + imageFileName); 
            
            if (file.exists()) 
                file.delete(); 
            
            persona.setFoto(null); 
            imageViewFoto.setImage(null); 
        } 
        else if (result.get() == buttonTypeMantener) 
        { 
            persona.setFoto(null); 
            imageViewFoto.setImage(null); 
        }
    }
}
