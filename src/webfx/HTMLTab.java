/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.URL;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author bruno
 */
public class HTMLTab implements BrowserTab {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    private SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>((Node) browser);

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
}
