/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
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
    private SingleSelectionModel<Tab> selectionTab;
    private ConcurrentHashMap<Integer, BrowserTab> browserMap = new ConcurrentHashMap<>();
    private Locale locale;

    public void exit() {
        LOGGER.info("Exiting...");
        System.exit(0);
    }

    public void newTab() {
        LOGGER.info("New tab...");
        Tab tab = new Tab("New tab");
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectionTab.selectLast();
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
            int indexBrowserTab = selectionTab.getSelectedIndex();
            browserMap.remove(indexBrowserTab);
            tabPane.getTabs().remove(selectionTab.getSelectedIndex());
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
        LOGGER.log(Level.INFO, "Content-type: {0}", contentType);

        BrowserTab browserTab;
        if (contentType.startsWith("text/html") == false) {
            browserTab = new FXTab();
            browserTab.goTo(url, locale);
        } else {
            browserTab = new HTMLTab();
            browserTab.goTo(url);
        }

        selectionTab.getSelectedItem().textProperty().bind(browserTab.titleProperty());
        selectionTab.getSelectedItem().contentProperty().bind(browserTab.contentProperty());
        browserMap.put(selectionTab.getSelectedIndex(), browserTab);
    }

    public void initialize() {
        urlField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) {
                    urlField.textProperty().unbind();
                } else if (selectedBrowser() != null) {
                    urlField.textProperty().bind(selectedBrowser().locationProperty());
                }
            }
        });

        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> change) {
                ObservableList<? extends Tab> tabs = change.getList();

                // disabled the close tab menu item if selected tab is not cloeable
                closeTab.disableProperty().bind(selectionTab.getSelectedItem().closableProperty().not());

                // set the first tab closeable if more than one tab
                tabs.get(0).setClosable(tabs.size() > 1);

                // set others tab closeable, if they exist
                for (int i = 1; i < tabs.size(); i++) {
                    tabs.get(i).setClosable(true);
                }
            }
        });

        selectionTab = tabPane.selectionModelProperty().getValue();

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                LOGGER.info("selecao da tab mudou");
                if (selectedBrowser() == null) {
                    LOGGER.info("nao tem browser selecionado");
                    urlField.textProperty().unbind();
                    urlField.textProperty().setValue("");
                    urlField.setText("");
                } else {
                    LOGGER.info("existe um browser selecionado");
                    urlField.textProperty().bind(selectedBrowser().locationProperty());
                }
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
        return browserMap.get(selectionTab.getSelectedIndex());
    }

    void setLocale(Locale locale) {
        this.locale = locale;
    }
}
