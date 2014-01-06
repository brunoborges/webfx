/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * {@literal WebFXView} is a {@link javafx.scene.Node} that manages an
 * {@link FXMLLoader} and a {@link ScriptEngine}, and displays its content. The
 * associated {@literal ScriptEngine} is created automatically at construction
 * time and cannot be changed afterwards.
 *
 */
public class WebFXView extends AnchorPane {

    private static final Logger LOGGER = Logger.getLogger(WebFXView.class.getName());
    private FXMLLoader fxmlLoader;
    private Locale locale;
    private ScriptEngine scriptEngine;
    private PageContext pageContext;
    private ResourceBundle resourceBundle;
    private final SimpleObjectProperty<URL> urlProperty = new SimpleObjectProperty<>();
    private NavigationContext navigationContext;
    private final ReadOnlyStringProperty titleProperty = new SimpleStringProperty();

    public WebFXView() {
        setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        getStyleClass().add("webfx-view");
        setFocusTraversable(true);
    }

    public WebFXView(URL url) {
        this();
        this.urlProperty.set(url);
        load();
    }

    WebFXView(NavigationContext navigationContext) {
        this();
        this.navigationContext = navigationContext;
    }

    WebFXView(URL url, NavigationContext navContext) {
        this();
        this.navigationContext = navContext;
        this.urlProperty.set(url);
        load();
    }

    /**
     * Returns the {@code PageContext} object.
     */
    public final PageContext getPageContext() {
        return pageContext;
    }

    public ReadOnlyObjectProperty<URL> getURLProperty() {
        return urlProperty;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setURL(URL url) {
        this.urlProperty.set(url);
    }

    public final void load() {
        Platform.runLater(() -> internalLoad());
    }

    private void internalLoad() {
        pageContext = new PageContext(urlProperty.get());

        initLocalization();

        try {
            fxmlLoader = new FXMLLoader(pageContext.getLocation(), resourceBundle);
            Node loadedNode = (Node) fxmlLoader.load();

            setTopAnchor(loadedNode, 0.0);
            setBottomAnchor(loadedNode, 0.0);
            setLeftAnchor(loadedNode, 0.0);
            setRightAnchor(loadedNode, 0.0);

            getChildren().add(loadedNode);

            hackScriptEngine(fxmlLoader);

            if (scriptEngine != null) {
                // dirty javascript initializer
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "ScriptEngine.LANGUAGE: {0}", scriptEngine.get(ScriptEngine.LANGUAGE));
                    LOGGER.log(Level.FINE, "ScriptEngine.LANGUAGE_VERSION: {0}", scriptEngine.get(ScriptEngine.LANGUAGE_VERSION));
                    LOGGER.log(Level.FINE, "ScriptEngine.NAME: {0}", scriptEngine.get(ScriptEngine.NAME));
                    LOGGER.log(Level.FINE, "ScriptEngine.ENGINE: {0}", scriptEngine.get(ScriptEngine.ENGINE));
                    LOGGER.log(Level.FINE, "ScriptEngine.ENGINE_VERSION: {0}", scriptEngine.get(ScriptEngine.ENGINE_VERSION));
                    LOGGER.log(Level.FINE, "ScriptEngine.FILENAME: {0}", scriptEngine.get(ScriptEngine.FILENAME));
                    LOGGER.log(Level.FINE, "ScriptEngine.toString: {0}", scriptEngine.toString());
                }

                Bindings wfxb = scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE);
                wfxb.put("__webfx_i18n", resourceBundle);
                wfxb.put("__webfx_navigation", navigationContext);

                scriptEngine.eval("if (typeof $webfx == 'undefined') $webfx = {title:'Untitled'};");
                scriptEngine.eval("if (typeof $webfx.initWebFX == 'function') $webfx.initWebFX();");
                scriptEngine.eval("$webfx.i18n = __webfx_i18n; $webfx.navigation = __webfx_navigation");

                loadTitle();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ScriptException ex) {
            Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTitle() {
        String title = "Untitled";
        if (scriptEngine != null) {
            try {
                Object objTitle = scriptEngine.eval("$webfx.title");
                title = objTitle.toString();

                LOGGER.log(Level.INFO, "Title found: {0}", title);

                if (resourceBundle != null && title.startsWith("%") && resourceBundle.containsKey(title.substring(1))) {
                    title = resourceBundle.getString(title.substring(1));
                    LOGGER.log(Level.INFO, "Actual title: {0}", title);
                }
            } catch (ScriptException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        final String titleToSet = title;
        Platform.runLater(() -> ((SimpleStringProperty) titleProperty).set(titleToSet));
    }

    public ReadOnlyStringProperty getTitleProperty() {
        return titleProperty;
    }

    /**
     * Hack needed while FXMLLoader does not exposes the ScriptEngine object in
     * case &lt;fx:script&gt; is used. Please see
     * <a href="https://javafx-jira.kenai.com/browse/RT-33264">RT-33264</a>
     *
     * @param loader
     */
    private void hackScriptEngine(FXMLLoader loader) {
        try {
            Field fse = loader.getClass().getDeclaredField("scriptEngine");
            fse.setAccessible(true);
            scriptEngine = (ScriptEngine) fse.get(loader);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void initLocalization() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;
        ResourceBundleLoader rbl = new ResourceBundleLoader(pageContext, _locale);
        resourceBundle = rbl.findBundle();
    }

}
