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
package webfx.applet;

import java.applet.Applet;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import webfx.URLVerifier;

/**
 * <p>
 * This is a proof of concept that allows an Applet to be executed either
 * through Live Connect on supported browsers, or through a JavaFX browser using
 * WebFX Component, such as WebFX Browser.</p>
 *
 * <p>
 * This class may also work with any JavaFX WebView browser implementation.</p>
 *
 * @author Bruno Borges <bruno.borges at oracle dot com>
 */
public class JSObjectFX extends JSObject {

    private static final Map<Applet, WebEngine> LOADED_APPLETS = Collections.synchronizedMap(new HashMap<Applet, WebEngine>());

    public static void enableAppletSupport(WebView webView) {
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue ov, State oldState, State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    injectApplets();
                }
            }

            private void injectApplets() throws JSException, DOMException {
                NodeList applets = webView.getEngine().getDocument().getElementsByTagName("applet");

                Logger.getLogger(JSObjectFX.class.getName()).log(Level.INFO, "Found {0} applets in current page.", applets.getLength());

                for (int i = 0; i < applets.getLength(); i++) {
                    try {
                        Node applet = applets.item(i);
                        String code = applet.getAttributes().getNamedItem("code").getNodeValue();
                        String appletName = applet.getAttributes().getNamedItem("name").getNodeValue();
                        // TODO archive may have multiple JARs defined
                        String archive = applet.getAttributes().getNamedItem("archive").getNodeValue();

                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Loading Applet class [name={0}; code={1}]", new Object[]{appletName, code});

                        String location = new URLVerifier(webView.getEngine().getLocation()).getBasePath().toString() + archive;
                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Location of Applet JAR file: {0}", location);

                        URLClassLoader ucl = new URLClassLoader(new URL[]{new URL(location)}, JSObjectFX.class.getClassLoader());
                        Class appletClazz = ucl.loadClass(code);

                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Going to create newInstance()");
                        Applet found = (Applet) appletClazz.newInstance();
                        LOADED_APPLETS.put(found, webView.getEngine());

                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Going to inject applet object into DOM");
                        injectJavascriptObjects(appletName, found);

                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Going to call Applet.init()");
                        found.init();

                        Logger.getLogger(JSObjectFX.class.getName())
                                .log(Level.INFO, "Going to call Applet.start()");
                        found.start();
                        // Object found = determineMappedObjectToApplet(appletClazz);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException ex) {
                        Logger.getLogger(JSObjectFX.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            private void injectJavascriptObjects(String appletName, Object found) throws JSException {
                JSObject doc = (JSObject) webView.getEngine().executeScript("window");
                String internalName = "fx__applet__" + appletName;
                doc.setMember(internalName, found);
                doc.setMember(appletName, found);
                doc.eval("$appletfx.setAppletFromFX('" + appletName + "'," + internalName + ")");
                doc.eval("$appletfx.fxApplet = new Array()");
            }
        });

    }

    private final JSObject netscapeJSObject;

    public static JSObject getWindow(Applet applet) {
        if (LOADED_APPLETS.containsKey(applet)) {
            return new JSObjectFX(LOADED_APPLETS.get(applet));
        } else {
            return JSObject.getWindow(applet);
        }
    }

    private JSObjectFX(WebEngine webEngine) {
        netscapeJSObject = (JSObject) webEngine.executeScript("window");
    }

    @Override
    public Object call(String methodName, Object[] args) throws JSException {
        return netscapeJSObject.call(methodName, args);
    }

    @Override
    public Object eval(String s) throws JSException {
        return netscapeJSObject.eval(s);
    }

    @Override
    public Object getMember(String name) throws JSException {
        return netscapeJSObject.getMember(name);
    }

    @Override
    public void setMember(String name, Object value) throws JSException {
        netscapeJSObject.setMember(name, value);
    }

    @Override
    public void removeMember(String name) throws JSException {
        netscapeJSObject.removeMember(name);
    }

    @Override
    public Object getSlot(int index) throws JSException {
        return netscapeJSObject.getSlot(index);
    }

    @Override
    public void setSlot(int index, Object value) throws JSException {
        netscapeJSObject.setSlot(index, value);
    }
}
