/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author bruno
 */
public class BrowserFXController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    public TabPane tabPane;
    public TextField urlField;
    public MenuItem closeTab;
    private SingleSelectionModel<Tab> selectedTab;

    public void exit() {
        LOGGER.info("Exiting...");
        System.exit(0);
    }

    public void newTab() {
        LOGGER.info("New tab...");
        tabPane.getTabs().add(new Tab("New tab"));
    }

    public void closeTab() {
        LOGGER.info("Close Tab...");
        if (tabPane.getTabs().size() > 1) {
            tabPane.getTabs().remove(selectedTab.getSelectedIndex());
        }
                
    }
    
    public void getLocalFXML()  {
        try {
            FileChooser chooser = new FileChooser();                  
            ExtensionFilter extensionFilter = new ExtensionFilter(" FXML Files (*.fxml)", "*.fxml");
            chooser.getExtensionFilters().add(extensionFilter);
            File selectedFile =chooser.showOpenDialog(null);
            FXMLLoader loader=new FXMLLoader();            
            Parent root = loader.load(getClass().getResource(selectedFile.getName()));
            selectedTab.getSelectedItem().setContent(root);
        } catch (IOException ex) {
            Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    

    public void openFXPage() {
        try {
                    
            String sUrl = urlField.getText();

            URL url = getClass().getResource(sUrl);

            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(url);

            selectedTab.getSelectedItem().setContent(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        LOGGER.info("Valid URL!");

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> change) {
                closeTab.disableProperty().set(change.getList().size() == 1);
            }
        });

        selectedTab = tabPane.selectionModelProperty().getValue();
    }
}
