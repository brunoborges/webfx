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
package com.webfx;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

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
                ScriptEngineFactory seFactory = scriptEngine.getFactory();
                LOGGER.log(Level.INFO, "ScriptEngine.LANGUAGE: {0}", seFactory.getLanguageName());
                LOGGER.log(Level.INFO, "ScriptEngine.LANGUAGE_VERSION: {0}", seFactory.getLanguageVersion());
                LOGGER.log(Level.INFO, "ScriptEngine.NAMES: {0}", seFactory.getNames());
                LOGGER.log(Level.INFO, "ScriptEngine.ENGINE: {0}", seFactory.getEngineName());
                LOGGER.log(Level.INFO, "ScriptEngine.ENGINE_VERSION: {0}", seFactory.getEngineVersion());
                LOGGER.log(Level.INFO, "ScriptEngine.FILENAMES: {0}", seFactory.getExtensions());
                LOGGER.log(Level.INFO, "ScriptEngine.toString: {0}", seFactory.toString());

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
