/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package webfx.browser;

import java.io.File;
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
        try {
            this.location = new URL(location);
        } catch (MalformedURLException e) {
            File f = new File(location.trim());
            if (f.isAbsolute() && f.exists()) {
                this.location = f.toURI().toURL();
            } else {
                this.location = new URL("http://" + location);
            }
        }
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
