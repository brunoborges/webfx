/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import webfx.context.NavigationContext;

/**
 *
 * @author bruno
 */
public class FXTab implements BrowserTab {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    private ReadOnlyStringWrapper titleProperty = new ReadOnlyStringWrapper();
    private ReadOnlyStringWrapper locationProperty = new ReadOnlyStringWrapper();
    private FXMLLoader loader;
    private Locale locale;
    private URL url;
    private SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();
    private ScriptEngine scriptEngine;
    private PageContext pageContext;

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    @Override
    public void forward() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void back() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goTo(URL url, Locale locale) {
        this.url = url;
        this.locale = locale;
        this.scriptEngine = null;
        this.contentProperty.set(null);
        this.titleProperty.set(null);
        this.locationProperty.set(null);
        this.pageContext = new PageContext(url);

        try {
            ResourceBundleLoader rbl = new ResourceBundleLoader(pageContext, locale);
            ResourceBundle resourceBundle = rbl.findBundle();

            loader = new FXMLLoader(url, resourceBundle);
            Node loadedNode = (Node) loader.load();

            hackCSS(loadedNode);

            locationProperty.set(url.toString());
            contentProperty.set(loadedNode);

            hackScriptEngine(loader);
            if (scriptEngine != null) {
                // title
                String title = extractTitle(loader);
                titleProperty.set(title);

                // i18n
                scriptEngine.put("__webfx_resourceBundle", resourceBundle);
                scriptEngine.eval("webfx.i18n = __webfx_resourceBundle;");

                // navigation
                scriptEngine.put("__webfx_navigation", getNavigationContext());
                scriptEngine.eval("webfx.navigation = __webfx_navigation;");
            }
        } catch (ScriptException ex) {
            Logger.getLogger(FXTab.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return titleProperty;
    }

    @Override
    public ReadOnlyStringProperty locationProperty() {
        return locationProperty;
    }

    @Override
    public void reload() {
        goTo(url, locale);
    }

    @Override
    public void stop() {
        contentProperty.setValue(null);
    }

    private String extractTitle(FXMLLoader loader) {
        String title = "Unknow";
        if (scriptEngine == null) {
            return title;
        }

        try {
            Object objTitle = scriptEngine.eval("webfx !== null ? webfx.title : 'Untitled'");
            title = objTitle.toString();

            ResourceBundle rb = loader.getResources();

            if (title.startsWith("%") && rb.containsKey(title.substring(1))) {
                title = rb.getString(title.substring(1));
            }

            return title;
        } catch (ScriptException ex) {
            Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return title;
    }

    private void hackScriptEngine(FXMLLoader loader) {
        try {
            Field fse = loader.getClass().getDeclaredField("scriptEngine");
            fse.setAccessible(true);
            scriptEngine = (ScriptEngine) fse.get(loader);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void goTo(URL url) {
        goTo(url, Locale.getDefault());
    }

    @Override
    public void setTabManager(TabManager tm) {
    }

    @Override
    public NavigationContext getNavigationContext() {
        return new NavigationContextImpl(this, pageContext);
    }

    private void hackCSS(Node loadedNode) {
        if (loadedNode instanceof Parent == false) {
            return;
        }

        Parent parent = (Parent) loadedNode;
        ObservableList<String> styles = parent.getStylesheets();
        List<String> fixedStyles = new ArrayList<String>(styles.size());
        for(String stylesheet : styles) {
            fixedStyles.add(pageContext.getBasePath().toString() + "/" + stylesheet);
        }
        styles.clear();
        styles.addAll(fixedStyles);
    }
}
