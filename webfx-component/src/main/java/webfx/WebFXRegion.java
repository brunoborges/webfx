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
package webfx;

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
