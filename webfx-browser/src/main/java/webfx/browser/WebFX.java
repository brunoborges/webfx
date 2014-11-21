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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import webfx.WebFXView;

/**
 *
 * @author bruno
 */
public class WebFX extends Application {

    private static final Logger LOGGER = Logger.getLogger(WebFX.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("browser.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        stage.getIcons().addAll(
                new Image("/webfx/browser/icons/globe_16.png", 16, 16, true, true),
                new Image("webfx/browser/icons/globe_32.png", 32, 32, true, true),
                new Image("webfx/browser/icons/globe_64.png", 64, 64, true, true)
        );
        BrowserFXController controller = fxmlLoader.getController();
        controller.setLocale(getCurrentLocale());
        Scene scene = new Scene(root);

        BrowserShortcuts shortcuts = new BrowserShortcuts(scene);
        shortcuts.setup(controller);

        stage.setTitle("WebFX Browser");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //turnSecurityManagerOn();

        launch(args);
    }
    
    private static void turnSecurityManagerOn(){
        //For good measure, turn the SecurityManager on.
        //Consider using OSGI http://moi.vonos.net/java/osgi-security/ in a future version
        final Path tempFile;
        final Path thisJar;
        final Path componentJarToLockDown;
        
        try {
            thisJar = Paths.get(getPathFor(WebFX.class)).toAbsolutePath();
            //Start locking down other JARs
            componentJarToLockDown = Paths.get(getPathFor(WebFXView.class)).toAbsolutePath();
            tempFile = Files.createTempFile("webfx", ".policy");
            
            if(Files.isSameFile(thisJar, componentJarToLockDown)){
                throw new SecurityException("Developer error, combined different-privileged JARs into one.");
            }
        } catch (IOException ex) {
            throw new SecurityException("Unable to create temporary WebFX security file.", ex);
        } catch (URISyntaxException ex) {
            throw new SecurityException("Unable to determine path of the JARs", ex);
        }

        try (final InputStream in = WebFX.class.getResourceAsStream("webfx.policy");) {
            String policyText = new Scanner(in).useDelimiter("\\A").next();
            final String webfxPath = Files.isDirectory(thisJar) ? thisJar.toString() + "/-" : thisJar.toString();
            policyText = policyText.replace("WEBFX_PATH", webfxPath.replaceAll("\\\\", "/"));
            policyText = policyText.replace("WEBFX_COMPONENT", componentJarToLockDown.toString().replaceAll("\\\\", "/"));
            Files.write(tempFile, policyText.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(WebFX.class.getName()).log(Level.SEVERE, "Unable to extract WebFX security file.", ex);
        }
        final String policyFileString = tempFile.toAbsolutePath().toString();
        System.setProperty("java.security.policy", policyFileString);
        System.setSecurityManager(new SecurityManager());
        
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException ex) {
            Logger.getLogger(WebFX.class.getName()).log(Level.SEVERE, "Unable to delete temporary WebFX file after use.", ex);
        }
        
        if(System.getSecurityManager()==null){
            throw new RuntimeException("SecurityManager not turned on.");
        }
    }
    
    private static URI getPathFor(Class clazz) throws URISyntaxException{
        return clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
    }

    private Locale getCurrentLocale() {
        Map<String, String> namedParams = getParameters().getNamed();

        String languageParamObj = null;
        String countryParamObj = null;

        if (namedParams != null) {
            languageParamObj = namedParams.get("language");
            countryParamObj = namedParams.get("country");
        }

        Locale locale = Locale.getDefault();
        LOGGER.log(Level.INFO, "Locale: {0}", locale);

        if ((languageParamObj != null)
                && ((String) languageParamObj).trim().length() > 0) {
            if ((countryParamObj != null)
                    && ((String) countryParamObj).trim().length() > 0) {
                locale = new Locale(((String) languageParamObj).trim(),
                        ((String) countryParamObj).trim());
            } else {
                locale = new Locale(((String) languageParamObj).trim());
            }
        }

        return locale;
    }
}
