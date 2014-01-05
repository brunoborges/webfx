/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import com.webfx.NavigationContext;
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
                                EventTarget eventTarget = event.getTarget();

                                if (eventTarget instanceof HTMLAnchorElement == false) {
                                    return;
                                }

                                HTMLAnchorElement hrefObj = (HTMLAnchorElement) event.getTarget();
                                String href = hrefObj.getHref();
                                if (href.endsWith(".fxml")) {
                                    try {
                                        getTabManager().openInNewTab(new URL(href));
                                    } catch (MalformedURLException ex) {
                                        Logger.getLogger(HTMLTab.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    event.preventDefault();
                                }
                            }
                        }, true);
                    }
                }
            }
        });
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
    public void stop() {
        webEngine.getLoadWorker().cancel();
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

    @Override
    public NavigationContext getNavigationContext() {
        return new NavigationContext() {

            @Override
            public void reload() {
                webEngine.reload();
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
                goTo(url.toString());
            }

            @Override
            public void goTo(String location) {
                webEngine.load(location);
            }

        };
    }

    @Override
    public boolean isStoppable() {
        return true;
    }
}
