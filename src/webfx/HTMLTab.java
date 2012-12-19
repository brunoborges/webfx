/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

/**
 *
 * @author bruno
 */
public class HTMLTab implements BrowserTab {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    final WebView browser;
    final WebEngine webEngine;
    private SimpleObjectProperty<Node> contentProperty;
    private TabManager tabManager;

    public HTMLTab() {
        browser = new WebView();
        webEngine = browser.getEngine();
        contentProperty = new SimpleObjectProperty<>((Node) browser);
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldv, State newv) {
                if (newv == State.SUCCEEDED) {
                    Document document = (Document) webEngine.executeScript("document");
                    NodeList nodeList = document.getElementsByTagName("a");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        EventTarget n = (EventTarget) nodeList.item(i);
                        n.addEventListener("click", new EventListener() {
                            @Override
                            public void handleEvent(Event event) {
                                HTMLAnchorElement hrefObj = (HTMLAnchorElement) event.getTarget();
                                String href = hrefObj.getHref();
                                InputStream is = null;
                                try {
                                    URL url = new URL(href);
                                    is = url.openStream();
                                    
                                    File file = new File(url.getFile());
                                    if (file.getName().contains(".fxml")) {
                                        getTabManager().openInNewTab(url);
                                        event.preventDefault();
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(HTMLTab.class.getName()).log(Level.SEVERE, null, ex);
                                } finally {
                                    try {
                                        is.close();
                                    } catch (IOException ex) {
                                        Logger.getLogger(HTMLTab.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }, true);
                    }
                }
            }
        });
    }

    @Override
    public void back() {
        webEngine.executeScript("history.back()");
    }

    @Override
    public void forward() {
        webEngine.executeScript("history.forward()");
    }

    @Override
    public void goTo(URL url) {
        webEngine.load(url.toString());
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return webEngine.titleProperty();
    }

    @Override
    public ReadOnlyStringProperty locationProperty() {
        return webEngine.locationProperty();
    }

    public ReadOnlyBooleanProperty loadingProperty() {
        return webEngine.getLoadWorker().runningProperty();
    }

    @Override
    public void reload() {
        webEngine.reload();
    }

    @Override
    public void stop() {
        webEngine.getLoadWorker().cancel();
    }

    @Override
    public void goTo(URL url, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    @Override
    public void setTabManager(TabManager tm) {
        this.tabManager = tm;
    }
    
    public TabManager getTabManager() {
        return tabManager;
    }
}
