/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public class URLVerifier {

    private URL location;
    private URL basePath;
    private String pageName;
    private boolean fxml;

    public URLVerifier(String location) throws MalformedURLException {
        if (!location.startsWith("http://") && !location.startsWith("https://")) {
            location = "http://" + location;
        }

        this.location = new URL(location);
        this.basePath = extractBasePath();
    }

    public URLVerifier(URL location) {
        this.location = location;
        this.basePath = extractBasePath();
    }

    private URL extractBasePath() {
        int lastSlash = location.getPath().lastIndexOf('/');

        if (lastSlash == -1) {
            pageName = "index";
            return location;
        }

        String file = location.getPath();
        String path = file.substring(0, lastSlash);
        URL base = null;

        try {
            base = new URL(location.getProtocol(), location.getHost(), location.getPort(), path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(URLVerifier.class.getName()).log(Level.SEVERE, null, ex);
        }

        pageName = file.substring(lastSlash + 1);
        int indexOfExtension = pageName.indexOf('.');
        if (indexOfExtension != 1) {
            String extension = file.substring(file.lastIndexOf('.') + 1);

            if ("fxml".equals(extension)) {
                fxml = true;
                pageName = pageName.substring(0, indexOfExtension);
            }
        }

        return base;
    }

    public URL getBasePath() {
        return basePath;
    }

    public URL getLocation() {
        return location;
    }

    /**
     * @return the pageName
     */
    public String getPageName() {
        return pageName;
    }

    public boolean isFxml() {
        return fxml;
    }
}
