/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author bruno
 */
public class BrowserFXController implements TabManager {

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
    private PageContext pageContext;

    public void exit() {
        LOGGER.info("Exiting...");
        System.exit(0);
    }

    public void newTab() {
        Tab tab = new Tab("New tab");
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectionTab.selectLast();
    }

    public void stop() {
        selectedBrowser().stop();
    }

    public void reload() {
        selectedBrowser().getNavigationContext().reload();
    }

    public void back() {
        selectedBrowser().getNavigationContext().back();
    }

    public void forward() {
        selectedBrowser().getNavigationContext().forward();
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
        try {
            openFXPage(new PageContext(urlField.getText()));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void openFXPage(PageContext pageContext) {
        Platform.runLater(() -> {
            this.pageContext = pageContext;
            URL url = pageContext.getLocation();

            BrowserTab browserTab;
            if (pageContext.isFxml()) {
                browserTab = new FX2Tab();
                browserTab.getNavigationContext().goTo(url/*, locale*/);
            } else {
                browserTab = new HTMLTab();
                browserTab.getNavigationContext().goTo(url);
            }

            browserTab.setTabManager(this);
            selectionTab.getSelectedItem().contentProperty().bind(browserTab.contentProperty());
            browserMap.put(selectionTab.getSelectedIndex(), browserTab);
            urlField.textProperty().bind(browserTab.locationProperty());
            stopButton.disableProperty().set(!browserTab.isStoppable());
            selectionTab.getSelectedItem().textProperty().bind(browserTab.titleProperty());
            LOGGER.log(Level.INFO, "Title used for new tab: {0}", browserTab.titleProperty().get());
        });
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

    @Override
    public void openInNewTab(URL url) {
        newTab();
        openFXPage(new PageContext(url));
    }
}
