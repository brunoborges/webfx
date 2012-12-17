/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.URL;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author bruno
 */
public class HTMLTab implements BrowserTab {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
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
    public WebView getContent() {
        return browser;
    }
    
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
}
