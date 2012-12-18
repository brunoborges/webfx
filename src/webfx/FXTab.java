/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author bruno
 */
public class FXTab implements BrowserTab {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    private ReadOnlyStringWrapper titleProperty = new ReadOnlyStringWrapper();
    private FXMLLoader loader;
    private Locale locale;
    private URL url;
    private SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();
    private ScriptEngine scriptEngine;

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

        try {
            File file = new File(url.getFile());
            String filename = file.getName();
            LOGGER.info(filename);

            Iterable<String> bundleNames = constructBundleFileNames(file, locale);
            ResourceBundle resourceBundle = null;

            for (String bundleName : bundleNames) {
                URL urlBundle = new URL(url.getProtocol(), url.getHost(), url.getPort(), bundleName);

                try (InputStream bundleIS = urlBundle.openStream()) {
                    resourceBundle = new PropertyResourceBundle(bundleIS);
                    break;
                } catch (FileNotFoundException e) {
                    LOGGER.log(Level.WARNING, "Bundle not found: {0}", bundleName);
                }
            }

            loader = new FXMLLoader(url, resourceBundle);
            Node loadedNode = (Node) loader.load();

            loadScriptEngine(loader);

            String title = extractTitle(loader);
            titleProperty.set(title);

            contentProperty.set(loadedNode);

            if (scriptEngine != null) {
                scriptEngine.put("_resourceBundle", resourceBundle);
                scriptEngine.eval("webfx.resourceBundle = _resourceBundle");
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reload() {
        goTo(url, locale);
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String extractTitle(FXMLLoader loader) {
        String title = "Unknow";

        try {
            title = scriptEngine.eval("webfx.title").toString();

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

    private void loadScriptEngine(FXMLLoader loader) {
        try {
            Field fse = loader.getClass().getDeclaredField("scriptEngine");
            fse.setAccessible(true);
            scriptEngine = (ScriptEngine) fse.get(loader);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(BrowserFXController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Iterable<String> constructBundleFileNames(File file, Locale locale) {
        String path = file.getParent();
        String filename = file.getName();
        filename = filename.substring(0, filename.indexOf('.'));

        List<String> names = new ArrayList<>();
        String l0 = new Locale(locale.getLanguage(), locale.getCountry()).toString();
        String l1 = new Locale(locale.getLanguage()).toString();
        names.add(new File(path, filename + "_" + l0 + ".properties").toString());
        names.add(new File(path, filename + "_" + l1 + ".properties").toString());
        names.add(new File(path, filename + ".properties").toString());

        return Collections.unmodifiableList(names);
    }

    @Override
    public void goTo(URL url) {
        goTo(url, Locale.getDefault());
    }
}
