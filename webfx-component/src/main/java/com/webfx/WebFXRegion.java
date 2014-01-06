/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public final class WebFXRegion extends AnchorPane {

    private URL url;
    private WebFXView defaultView;
    private final NavigationContext navigationContext;
    private final ReadOnlyStringProperty currentTitle = new SimpleStringProperty();
    private Locale locale;

    public WebFXRegion() {
        navigationContext = new NavigationContextImpl();
    }

    public WebFXRegion(URL url) {
        this();
        loadUrl(url);
    }

    public ReadOnlyStringProperty getCurrentViewTitleProperty() {
        return currentTitle;
    }

    public final void setUrl(URL url) {
        this.url = url;
    }

    public void loadUrl(URL url) {
        setUrl(url);
        load();
    }

    public void load() {
        defaultView = new WebFXView(navigationContext);
        defaultView.setURL(url);
        defaultView.setLocale(locale);
        defaultView.load();
        getChildren().clear();
        getChildren().add(defaultView);

        setTopAnchor(defaultView, 0.0);
        setRightAnchor(defaultView, 0.0);
        setLeftAnchor(defaultView, 0.0);
        setBottomAnchor(defaultView, 0.0);

        ((SimpleStringProperty) currentTitle).bind(defaultView.getTitleProperty());
    }

    public NavigationContext getNavigationContext() {
        return navigationContext;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    private class NavigationContextImpl implements NavigationContext {

        @Override
        public void forward() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void back() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void goTo(URL url) {
            WebFXRegion.this.loadUrl(url);
        }

        @Override
        public void goTo(String url) {
            URL destination = null;
            if (url.startsWith("file:/") || url.startsWith("jar:/") || url.startsWith("wfx:/") || url.startsWith("http:/") || url.startsWith("https:/")) {
                try {
                    destination = new URL(url);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                URL basePath = defaultView.getPageContext().getBasePath();
                try {
                    destination = new URL(basePath.toString() + "/" + url);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            goTo(destination);
        }

        @Override
        public void reload() {
            WebFXRegion.this.load();
        }
    }

}
