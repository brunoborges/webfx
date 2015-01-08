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
package webfx.scripting;

import groovy.lang.Script;
import groovy.util.Expando;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import javafx.scene.Scene;
import webfx.NavigationContext;

/**
 *
 * @author bruno
 */
public class ScriptingInitializer {

    private static final Logger LOGGER = Logger.getLogger(ScriptingInitializer.class.getName());
    private final ScriptEngine scriptEngine;
    private final ResourceBundle resourceBundle;
    private final NavigationContext navigationContext;
    private final Scene scene;
    private final String title;

    public ScriptingInitializer(ScriptEngine scriptEngine, ResourceBundle resourceBundle, NavigationContext navigationContext, Scene scene) {
        this.scriptEngine = scriptEngine;
        this.resourceBundle = resourceBundle;
        this.navigationContext = navigationContext;
        this.scene = scene;

        ScriptEngineFactory seFactory = scriptEngine.getFactory();
        LOGGER.log(Level.INFO, "ScriptEngine.LANGUAGE_NAME: {0}", seFactory.getLanguageName());
        LOGGER.log(Level.INFO, "ScriptEngine.LANGUAGE_VERSION: {0}", seFactory.getLanguageVersion());
        LOGGER.log(Level.INFO, "ScriptEngine.NAMES: {0}", seFactory.getNames());
        LOGGER.log(Level.INFO, "ScriptEngine.ENGINE: {0}", seFactory.getEngineName());
        LOGGER.log(Level.INFO, "ScriptEngine.ENGINE_VERSION: {0}", seFactory.getEngineVersion());
        LOGGER.log(Level.INFO, "ScriptEngine.FILENAMES: {0}", seFactory.getExtensions());
        LOGGER.log(Level.INFO, "ScriptEngine.toString: {0}", seFactory.toString());

        ScriptingInjector injector;

        String language = seFactory.getLanguageName().toLowerCase();
        if ("groovy".equals(language)) {
            injector = new GroovyImpl();
        } else {
            injector = new NashornImpl();
        }

        injector.init();
        title = injector.getTitle();
    }

    public String getPageTitle() {
        return this.title;
    }

    interface ScriptingInjector {

        public void init();

        public String getTitle();
    }

    class NashornImpl implements ScriptingInjector {

        @Override
        public void init() {
            try {
                scriptEngine.put("__webfx_i18n", resourceBundle);
                scriptEngine.put("__webfx_navigation", navigationContext);
                scriptEngine.put("__webfx_scene", scene);

                if (scriptEngine.get("$webfx") == null) {
                    scriptEngine.eval("$webfx = {title:'Untitled'};");
                }

                scriptEngine.eval("$webfx.i18n = __webfx_i18n;");
                scriptEngine.eval("$webfx.navigation = __webfx_navigation;");
                scriptEngine.eval("$webfx.scene = __webfx_scene;");
                scriptEngine.eval("if (typeof $webfx.initWebFX === 'function') $webfx.initWebFX();");
            } catch (ScriptException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public String getTitle() {
            Object objTitle = "Undefined";
            try {
                objTitle = scriptEngine.eval("$webfx.title");
            } catch (ScriptException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return objTitle.toString();
        }

    }

    class GroovyImpl implements ScriptingInjector {

        @Override
        public void init() {
            scriptEngine.put("__webfx_i18n", resourceBundle);
            scriptEngine.put("__webfx_navigation", navigationContext);
            scriptEngine.put("__webfx_scene", scene);

            Expando groovy_webfx = (Expando) scriptEngine.get("$webfx");
            if (groovy_webfx == null) {
                scriptEngine.put("$webfx", groovy_webfx = new Expando());
            } else if (groovy_webfx.getProperty("initWebFX") != null) {
                groovy_webfx.invokeMethod("initWebFX", null);
            }

            groovy_webfx.setProperty("i18n", resourceBundle);
            groovy_webfx.setProperty("navigation", navigationContext);
            groovy_webfx.setProperty("scene", scene);
        }

        @Override
        public String getTitle() {
            Object objTitle = "Undefined";
            try {
                objTitle = scriptEngine.eval("$webfx.title");
            } catch (ScriptException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return objTitle.toString();
        }

    }

}
