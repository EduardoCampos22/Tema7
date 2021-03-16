/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppAgendaJPRO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.jpro.webapi.JProApplication;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javafx.fxml.FXMLLoader;
import javax.persistence.Persistence;
import javafx.scene.layout.Pane;



/**
 *
 * @author Eduardo
 */
public class Main extends JProApplication
{       
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Override
    public void start(Stage primaryStage) throws IOException 
    {
        StackPane rootMain = new StackPane(); 
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AgendaView.fxml")); 
        Pane rootAgendaView = fxmlLoader.load(); 
        rootMain.getChildren().add(rootAgendaView);
        
        // Conexión a la BD creando los objetos EntityManager y
        // EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        em = emf.createEntityManager();
        
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
        agendaViewController.setEntityManager(em);
        agendaViewController.cargarTodasPersonas();
        
        Scene scene = new Scene(rootMain);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() throws Exception 
    {
        em.close();
        emf.close();
        
        try
        {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/AgendaDB;shutdown=true");
        }
        
        catch(SQLException ex) { }
    }    
}
