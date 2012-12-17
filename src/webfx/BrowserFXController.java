/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 * @author bruno
 */
public class BrowserFXController {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    /**
     * Components
     */
    @FXML
    private TabPane tabPane;
    @FXML
    private TextField urlField;
    @FXML
    private MenuItem closeTab;
    @FXML
    private Button stopButton;
    @FXML
    private Button reloadButton;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;
    /**
     * Internal
     */
    private SingleSelectionModel<Tab> selectedTab;
    private ConcurrentHashMap<Integer, BrowserTab> browserMap = new ConcurrentHashMap<>();

    public void exit() {
        LOGGER.info("Exiting...");
        System.exit(0);
    }

    public void newTab() {
        LOGGER.info("New tab...");
        Tab tab = new Tab("New tab");
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectedTab.selectLast();
    }

    public void stop() {
        selectedBrowser().stop();
    }

    public void reload() {
        selectedBrowser().reload();
    }

    public void back() {
        selectedBrowser().back();
    }

    public void forward() {
        selectedBrowser().forward();
    }

    public void closeTab() {
        LOGGER.info("Close Tab...");
        if (tabPane.getTabs().size() > 1) {
            tabPane.getTabs().remove(selectedTab.getSelectedIndex());
        }
    }

    public void openFXPage() {
        //try {
        String sUrl = urlField.getText();
        if (sUrl.indexOf("://") == -1) {
            sUrl = "http://" + sUrl;
        }

        URL url = null;
        URLConnection urlConnection = null;
        try {
            url = new URL(sUrl);
            urlConnection = url.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
        }

        String contentType = urlConnection.getContentType();
        LOGGER.info("Content-type: " + contentType);

        Parent page = null;

        if (contentType.startsWith("text/html") == false) {
            try {
                page = FXMLLoader.load(url);
                // access the ScriptEngine and extract the title from 'webfx' object
            } catch (IOException ex) {
                Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            HTMLTab htmlTab = new HTMLTab();
            page = htmlTab.getContent();
            htmlTab.goTo(url);
            browserMap.put(selectedTab.getSelectedIndex(), htmlTab);
            selectedTab.getSelectedItem().textProperty().bind(htmlTab.titleProperty());
        }

        selectedTab.getSelectedItem().setContent(page);
    }

    public void initialize() {
        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> change) {
                ObservableList<? extends Tab> tabs = change.getList();

                // disabled the close tab menu item if selected tab is not cloeable
                closeTab.disableProperty().bind(selectedTab.getSelectedItem().closableProperty().not());

                // set the first tab closeable if more than one tab
                tabs.get(0).setClosable(tabs.size() > 1);

                // set others tab closeable, if they exist
                for (int i = 1; i < tabs.size(); i++) {
                    tabs.get(i).setClosable(true);
                }
            }
        });

        selectedTab = tabPane.selectionModelProperty().getValue();

        tabPane.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<Tab>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<Tab>> ov, SingleSelectionModel<Tab> t, SingleSelectionModel<Tab> t1) {
                urlField.textProperty().bind(selectedBrowser().locationProperty());
            }
        });

        int size = 16;
        setButtonIcon(stopButton, "stop", size);
        setButtonIcon(backButton, "left", size);
        setButtonIcon(forwardButton, "right", size);
        setButtonIcon(reloadButton, "clock", size);
    }

    private void setButtonIcon(Button button, String icon, int size) {
        InputStream is = getClass().getResourceAsStream("icons/" + icon + "_" + size + ".png");
        Image block = new Image(is);
        ImageView iv = new ImageView(block);
        // add images to toolbar
        button.setGraphic(iv);
    }

    private BrowserTab selectedBrowser() {
        return browserMap.get(selectedTab.getSelectedIndex());
    }

    void setupShortcuts(Scene scene) {
        final ObservableMap<KeyCombination, Runnable> accelerators = scene.getAccelerators();

        accelerators.put(
                new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                newTab();
            }
        });
        accelerators.put(
                new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                closeTab();
            }
        });
        accelerators.put(
                new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
    }
}
