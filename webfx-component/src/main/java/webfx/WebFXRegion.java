/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.AnchorPane;
import webfx.contentdescriptors.ContentDescriptor;
import webfx.urlhandlers.URLHandler;
import webfx.urlhandlers.URLHandlersRegistry;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public final class WebFXRegion extends AnchorPane {

    private final SimpleStringProperty urlProperty = new SimpleStringProperty();
    private WebFXView defaultView;
    private final NavigationContext navigationContext;
    private final ReadOnlyStringProperty currentTitle = new SimpleStringProperty();
    private Locale locale;
    private ClassLoader cl = null;

    public WebFXRegion() {
        navigationContext = new NavigationContextImpl();
    }

    public WebFXRegion(URL url) {
        this();
        navigationContext.goTo(url);
    }

    public WebFXRegion(@NamedArg("url") String url) throws MalformedURLException {
        this();
        navigationContext.goTo(url);
    }

    public void setUrl(String url) {
        this.urlProperty.set(url);
    }

    public String getUrl() {
        return this.urlProperty.get();
    }

    public SimpleStringProperty urlProperty() {
        return this.urlProperty;
    }

    public ReadOnlyStringProperty getCurrentViewTitleProperty() {
        return currentTitle;
    }

    private void loadUrl(URL url) {
        setUrl(url.toString());
        load();
    }

    public void load() {
        if (getScene() != null && getScene().getStylesheets() != null) {
            getScene().getStylesheets().clear();
        }

        getChildren().clear();

        defaultView = new WebFXView(navigationContext, cl);
        try {
            defaultView.setURL(new URL(getUrl()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebFXRegion.class.getName()).log(Level.SEVERE, null, ex);
        }
        defaultView.setLocale(locale);
        defaultView.load();

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

        private class HistoryEntry {
            URL url;
            ClassLoader cl;

            private HistoryEntry(URL url, ClassLoader cl) {
                this.url = url;
                this.cl = cl;
            }
        }

        private int currentURLHistoryIndex = -1;
        private List<HistoryEntry> urlHistory = new ArrayList<>();

        @Override
        public void forward() {
            int nextIndex = currentURLHistoryIndex + 1;
            if (nextIndex < urlHistory.size()) {
                HistoryEntry entry = urlHistory.get(nextIndex);
                URL nextURL = entry.url;
                cl = entry.cl;
                currentURLHistoryIndex++;
                loadUrl(nextURL, false);
            }

        }

        @Override
        public void back() {
            if (currentURLHistoryIndex <= 0) {
                return; // can't go anywhere back
            }

            currentURLHistoryIndex--;
            HistoryEntry entry = urlHistory.get(currentURLHistoryIndex);
            URL previousURL = entry.url;
            cl = entry.cl;
            loadUrl(previousURL, false);
        }

        private void loadUrl(URL url, boolean incrementHistory) {
            WebFXRegion.this.loadUrl(url);

            if (incrementHistory) {
                HistoryEntry history = new HistoryEntry(url, cl);
                if (currentURLHistoryIndex == urlHistory.size() - 1) {
                    urlHistory.add(history);
                    currentURLHistoryIndex = urlHistory.size() - 1;
                } else {
                    ++currentURLHistoryIndex;
                    urlHistory.set(currentURLHistoryIndex, history);
                }
            }
        }

        @Override
        public void goTo(URL url) {
            URLHandler urlHandler = URLHandlersRegistry.getHandler(url);
            if (urlHandler != null) {
                URLHandler.Result handleResult = urlHandler.handle(url);
                if (handleResult.contentDescriptor == ContentDescriptor.FXML.instance()) {
                    try {
                        //resolve destination
                        url = url.openConnection().getURL();
                    } catch (IOException e) {
                    }
                    cl = handleResult.classLoader;
                } else {
                    //cannot render non FXML content
                    return;
                }
            }
            loadUrl(url, true);
        }

        private URL resolveDestination(String relPath) {
            PageContext pageContext = defaultView != null ? defaultView.getPageContext() : WebFXView.getCurrentContext();
            if (pageContext == null) return null;

            URL context = pageContext.getLocation();
            URL destination = null;
            try {
                destination = new URL(context, relPath);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
            }
            return destination;
        }

        @Override
        public void goTo(String url) {
            goTo(resolveDestination(url));
        }

        @Override
        public void goTo(String protocol, String relPath) {
            URL url = resolveDestination(relPath);
            try {
                url = url.getProtocol().equals(protocol)? url : new URL(protocol, url.getHost(), url.getPort(), url.getFile());
            } catch (MalformedURLException e) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, e);
                return;
            }
            goTo(url);
        }

        @Override
        public void reload() {
            WebFXRegion.this.load();
        }
    }

}
