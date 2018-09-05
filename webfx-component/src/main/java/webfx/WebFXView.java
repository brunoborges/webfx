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
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import javax.script.ScriptEngine;

import webfx.scripting.ScriptingInitializer;

/**
 * {@literal WebFXView} is a {@link javafx.scene.Node} that manages an
 * {@link FXMLLoader} and a {@link ScriptEngine}, and displays its content. The
 * associated {@literal ScriptEngine} is created automatically at construction
 * time and cannot be changed afterwards.
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public class WebFXView extends AnchorPane {

    private static final Logger LOGGER = Logger.getLogger(WebFXView.class.getName());

    //current page context used for loading embeded WebFXRegions relative to current
    private static final ThreadLocal<PageContext> curContext = new ThreadLocal<>();

    private FXMLLoader fxmlLoader;
    private Locale locale;
    private ScriptEngine scriptEngine;
    private PageContext pageContext;
    private ResourceBundle resourceBundle;
    private final SimpleObjectProperty<URL> urlProperty = new SimpleObjectProperty<>();
    private NavigationContext navigationContext;
    private final ReadOnlyStringProperty titleProperty = new SimpleStringProperty();
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private ClassLoader cl;

    public WebFXView() {
        setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        getStyleClass().add("webfx-view");
    }

    public WebFXView(@NamedArg("url") String url) throws MalformedURLException {
        this(new URL(url));
    }

    public WebFXView(URL url) {
        this();
        this.urlProperty.set(url);
        load();
    }

    WebFXView(NavigationContext navigationContext, ClassLoader cl) {
        this();
        this.navigationContext = navigationContext;
        this.cl = cl;
    }


    public static PageContext getCurrentContext() {
        return curContext.get();
    }

    /**
     * Returns the {@code PageContext} object.
     *
     * @return pageContext
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
        load();
    }

    public SimpleObjectProperty<URL> urlProperty() {
        return this.urlProperty;
    }

    public final void load() {
        if (loaded.get() == false) {
            Platform.runLater(this::internalLoad);
            loaded.set(true);
        }
    }

    public static boolean focusFirstChild(List<Node> children) {
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (n.isFocusTraversable() && !n.isDisabled()) {
                n.requestFocus();
                return true;
            }
            else if (n instanceof Parent) {
                if (focusFirstChild(((Parent)n).getChildrenUnmodifiable())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void internalLoad() {
        pageContext = new PageContext(urlProperty.get());

        initLocalization();

        try {
            final ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();

            if (cl != null) {
                Thread.currentThread().setContextClassLoader(cl);
            }

            PageContext oldPageContext = curContext.get();
            curContext.set(pageContext);

            fxmlLoader = new FXMLLoader(pageContext.getLocation(), resourceBundle);
            Node loadedNode = fxmlLoader.load();

            if (cl != null) {
                Thread.currentThread().setContextClassLoader(oldClassloader);
            }

            curContext.set(oldPageContext);

            setTopAnchor(loadedNode, 0.0);
            setBottomAnchor(loadedNode, 0.0);
            setLeftAnchor(loadedNode, 0.0);
            setRightAnchor(loadedNode, 0.0);

            getChildren().add(loadedNode);

            focusFirstChild(getChildren());

            hackScriptEngine(fxmlLoader);

            if (scriptEngine != null) {
                ScriptingInitializer si = new ScriptingInitializer(scriptEngine, resourceBundle, navigationContext, getScene());
                loadTitle(si.getPageTitle());
            } else {
                String path = pageContext.getLocation().getFile();
                int lastSlash = path.lastIndexOf('/');
                Platform.runLater(() -> ((SimpleStringProperty) titleProperty).set(path.substring(lastSlash + 1)));
            }
        } catch (IOException ex) {
            Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTitle(String _title) {
        String title = _title == null ? "Untitled" : _title;

        if (resourceBundle != null && title.startsWith("%") && resourceBundle.containsKey(title.substring(1))) {
            title = resourceBundle.getString(title.substring(1));
            LOGGER.log(Level.INFO, "Actual title: {0}", title);
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
        ResourceBundleLoader rbl = new ResourceBundleLoader(pageContext, locale);
        resourceBundle = rbl.findBundle();
    }
}
