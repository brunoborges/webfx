/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
class ResourceBundleLoader {

    private static final Logger LOGGER = Logger.getLogger(ResourceBundleLoader.class.getName());
    
    private final Locale locale;
    private final PageContext pageContext;

    ResourceBundleLoader(PageContext pageContext, Locale locale) {
        this.pageContext = pageContext;
        this.locale = locale;
    }

    protected ResourceBundle findBundle() {
        ResourceBundle found = null;

        Iterable<String> bundleNames = constructBundleFileNames();

        URL baseURL = pageContext.getBasePath();
        
        for (String bundleName : bundleNames) {
            try {
                URL urlBundle = new URL(baseURL.toString() + "/" + bundleName);

                try (InputStream bundleIS = urlBundle.openStream()) {
                    found = new PropertyResourceBundle(bundleIS);
                    break;
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Bundle not found: " + bundleName, ex);
                }
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return found;
    }

    private Iterable<String> constructBundleFileNames() {
        String filename = pageContext.getPageName();
        
        List<String> names = new ArrayList<>();
        String l0 = new Locale(locale.getLanguage(), locale.getCountry()).toString();
        String l1 = new Locale(locale.getLanguage()).toString();
        names.add(filename + "_" + l0 + ".properties");
        names.add(filename + "_" + l1 + ".properties");
        names.add(filename + ".properties");

        return Collections.unmodifiableList(names);
    }
}
