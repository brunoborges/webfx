/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public class PageContext {

    private final URL location;
    private URL basePath;
    private String pageName;

    public PageContext(URL location) {
        this.location = location;
        extractBasePath();
    }

    private void extractBasePath() {
        if (location.getPath() == null) {
            return;
        }

        int lastSlash = location.getPath().lastIndexOf('/');

        if (lastSlash == -1) {
            pageName = "index";
            basePath = location;
        }

        String file = location.getPath();
        String path = file.substring(0, lastSlash);
        URL base = null;

        try {
            base = new URL(location.getProtocol(), location.getHost(), location.getPort(), path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PageContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        pageName = file.substring(lastSlash + 1);
        int indexOfExtension = pageName.indexOf('.');
        if (indexOfExtension != 1) {
            String extension = file.substring(file.lastIndexOf('.') + 1);

            if (!"fxml".equals(extension)) {
                throw new IllegalArgumentException("This component only loads FXML pages. Point the URL property to an FXML file");
            }

            pageName = pageName.substring(0, indexOfExtension);
        }

        this.basePath = base;
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

}
