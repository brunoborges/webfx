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
package webfx.deck;

import webfx.deck.mbean.DeckServer;
import webfx.WebFXRegion;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 *
 * @author bruno
 */
public class DeckMain extends Application {

    private static final Logger LOGGER = Logger.getLogger(DeckMain.class.getName());

    private WebFXRegion fxView;
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        try {
            String url = System.getProperty("webfx.url");
            if (url == null) {
                fxView = new WebFXRegion();
                LOGGER.warning("No URL specified in CLI with -Dwebfx.url. You must connect through the MBean and invoke a URL.");
            } else {
                fxView = new WebFXRegion(new URL(url));
            }

            int screenWidth = Integer.parseInt(System.getProperty("webfx.width", String.valueOf(DEFAULT_WIDTH)));
            int screenHeight = Integer.parseInt(System.getProperty("webfx.height", String.valueOf(DEFAULT_HEIGHT)));

            startMBeanServer(fxView);

            StackPane root = new StackPane();
            root.getChildren().add(fxView);
            root.setPrefSize(screenWidth, screenHeight);

            Scene scene = new Scene(root, screenWidth, screenHeight);
            fxView.setOnKeyPressed(e -> {
                KeyCode keyCode = e.getCode();
                if (keyCode.equals(KeyCode.F5) || (keyCode.equals(KeyCode.R) && e.isControlDown())) {
                    reload();
                }

                if (keyCode.equals(KeyCode.LEFT) && e.isControlDown()) {
                    goBack();
                }

                if (keyCode.equals(KeyCode.RIGHT) && e.isControlDown()) {
                    goForward();
                }

                if (keyCode.equals(KeyCode.Q) && e.isControlDown()) {
                    System.exit(0);
                }
            });

            fxView.getCurrentViewTitleProperty().addListener((a, b, c) -> {
                primaryStage.setTitle(c);
            });
            primaryStage.setScene(scene);
            if (System.getProperty("os.arch").toUpperCase().contains("ARM")) {
                primaryStage.setFullScreen(true);
                primaryStage.setFullScreenExitHint("");
            }

            primaryStage.show();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void startMBeanServer(WebFXRegion fxView) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("webfx.deck.mbean:type=DeckServer");
            DeckServer server = new DeckServer(fxView);
            mbs.registerMBean(server, name);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    private void reload() {
        fxView.load();
    }

    private void goBack() {
        fxView.getNavigationContext().back();
    }

    private void goForward() {
        fxView.getNavigationContext().forward();
    }

}
