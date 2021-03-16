/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppAgendaJPRO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import AppAgendaJPRO.Persona;

/**
 * FXML Controller class
 *
 * @author Eduardo
 */



public class AgendaViewController implements Initializable 
{
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private Button buttonGuardar;
    @FXML
    private TextField textFieldApellidos;

    
    private Persona personaSeleccionada;
    @FXML
    private Button buttonNuevo;
    @FXML
    private Button buttonEditar;
    @FXML
    private Button buttonSuprimir;
    @FXML
    private AnchorPane rootAgendaView;
    
    
    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    } 
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(cellData -> {
            
            SimpleStringProperty property = new SimpleStringProperty();
            
            if (cellData.getValue().getProvincia()!=null)
            {
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            
            return property;
            });
        
        
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue)->{ 
                personaSeleccionada = newValue; 
                
                if (personaSeleccionada != null)
                { 
                    textFieldNombre.setText(personaSeleccionada.getNombre()); 
                    textFieldApellidos.setText(personaSeleccionada.getApellidos()); 
                } 
                else 
                { 
                    textFieldNombre.setText(""); 
                    textFieldApellidos.setText(""); 
                }
            });
                
    }

    
    public void cargarTodasPersonas()
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        Query queryPersonaFindAll = entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));        
    }

    @FXML
    private void OnActionButtonGuardar(ActionEvent event) 
    {
        if (personaSeleccionada != null)
        {
            // Actualiza los valoras Nombre y Apellidos del objeto Persona asignandole los valores
            // recogidos de los TextFields
            personaSeleccionada.setNombre(textFieldNombre.getText()); 
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            
            entityManager.getTransaction().begin(); 
            entityManager.merge(personaSeleccionada); 
            entityManager.getTransaction().commit();
            
            // Obtiene el numero de la fila seleccionada y asigna el nuevo objeto a esa fila
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex(); 
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            
            // Devuelve el foco al TableView para que el usuario pueda seguir moviendose por su contenido
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null); 
            tableViewAgenda.getFocusModel().focus(pos); 
            tableViewAgenda.requestFocus();
        }
    }

    @FXML
    private void OnActionButtonNuevo(ActionEvent event) 
    {
        try
        { 
            // Cargar la vista de detalle 
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PersonaDetalleView.fxml")); 
            Parent rootDetalleView = fxmlLoader.load(); 
                                    
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController(); 
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            
            //Intercambio de datos funcionales con el detalle 
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            
            personaSeleccionada = new Persona(); 
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, true);
            
            // Ocultar la vista de la lista 
            rootAgendaView.setVisible(false); 
            
            personaDetalleViewController.mostrarDatos();
            
            //Añadir la vista detalle al StackPane principal para que se muestre 
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot(); 
            rootMain.getChildren().add(rootDetalleView);                         
            
            
        } 
        catch (IOException ex){ Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex); }
    }

    @FXML
    private void OnActionButtonEditar(ActionEvent event) 
    {
        try
        { 
            // Cargar la vista de detalle 
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PersonaDetalleView.fxml")); 
            Parent rootDetalleView = fxmlLoader.load(); 
            
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController(); 
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            
            //Intercambio de datos funcionales con el detalle 
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, false);
            
            personaDetalleViewController.mostrarDatos();
            
            // Ocultar la vista de la lista 
            rootAgendaView.setVisible(false); 
            
            //AÃ±adir la vista detalle al StackPane principal para que se muestre 
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot(); 
            rootMain.getChildren().add(rootDetalleView); 
            
            
        } 
        catch (IOException ex){ Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex); }
    }

    @FXML
    private void OnActionButtonSuprimir(ActionEvent event) 
    {
        // Mostrara una alerta en el caso de que el usuario quiera suprimir  un registro
        // Dicha alerta mostrarÃ¡ el nombre y el apellido de la persona a suprimir
        Alert alert = new Alert(AlertType.CONFIRMATION); 
        alert.setTitle("Confirmar"); 
        alert.setHeaderText("¿Desea suprimir el siguiente registro?"); 
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos()); 
        Optional<ButtonType> result = alert.showAndWait(); 
        
        if (result.get()== ButtonType.OK)
        { 
            // Acciones a realizar si el usuario acepta
            entityManager.getTransaction().begin(); 
            entityManager.merge(personaSeleccionada); 
            entityManager.remove(personaSeleccionada); 
            entityManager.getTransaction().commit(); 
            
            tableViewAgenda.getItems().remove(personaSeleccionada); 
            tableViewAgenda.getFocusModel().focus(null); 
            tableViewAgenda.requestFocus();
        } 
        else 
        { 
            // Acciones a realizar si el usuario cancela     
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex(); 
            
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada); 
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null); 
            
            tableViewAgenda.getFocusModel().focus(pos); 
            tableViewAgenda.requestFocus();
        }
    }
    
}
