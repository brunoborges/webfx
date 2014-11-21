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
package webfx.browser.settings;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ecostlow
 */
public class SettingsController implements Initializable {

    private Consumer<Void> onClose = closeAction -> System.out.println("Closing");

    private final String[] proxyTypes = {"No Proxy", "Use System Settings", "Configure as below"};

    @FXML
    private ChoiceBox<String> proxyType;

    @FXML
    private TextField manualProxy;

    @FXML
    private CheckBox tls12;

    @FXML
    private CheckBox tls11;

    @FXML
    private CheckBox tls1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        proxyType.setItems(FXCollections.observableArrayList(proxyTypes));
        proxyType.getSelectionModel().selectedIndexProperty().addListener(this::alterView);

        final String currentManualProxy = System.getProperty("http.proxyHost");
        if (currentManualProxy != null) {
            final String joined = currentManualProxy + ':' + System.getProperty("http.proxyPort");
        }
        manualProxy.setVisible(currentManualProxy != null);
    }

    public void setOnClose(Consumer<Void> closeAction) {
        this.onClose = closeAction;
    }

    private void alterView(ObservableValue<? extends Number> something, Number previous, Number current) {
        manualProxy.setVisible(current.intValue() == 2);
    }

    public void saveAndClose() {
        doProxyConfig();
        doTLSConfig();

        onClose.accept(null);
    }

    private void doTLSConfig() {
        final List<String> enable = new ArrayList<>(3);
        check(tls1, "TLSv1", enable);
        check(tls11, "TLSv1.1", enable);
        check(tls12, "TLSv1.2", enable);
        final String joined = enable.isEmpty() ? "TLSv1,TLSv1.1,TLSv1.2" : enable.stream().collect(Collectors.joining(","));
        System.setProperty("https.protocols", joined);
        System.setProperty("deployment.security.SSLv3", String.valueOf(false));
        System.setProperty("deployment.security.SSLv2", String.valueOf(false));
    }

    private void check(CheckBox check, String value, List<String> enable) {
        if (check.isSelected()) {
            enable.add(value);
        }
        final String property = "deployment.security." + value;
        System.setProperty(property, String.valueOf(check.isSelected()));
    }

    private void doProxyConfig() {
        final String useSystemProxies = "java.net.useSystemProxies";
        final String httpProxyHost = "http.proxyHost";
        final String httpProxyPort = "http.proxyPort";
        final String httpsProxyHost = "https.proxyHost";
        final String httpsProxyPort = "https.proxyPort";

        final String[] properties = {useSystemProxies, httpProxyHost, httpProxyPort, httpsProxyHost, httpsProxyPort};

        switch (proxyType.getSelectionModel().selectedIndexProperty().get()) {
            case 0:
                Arrays.stream(properties).forEach(System.getProperties()::remove);
                break;
            case 1:
                Arrays.stream(properties).forEach(System.getProperties()::remove);
                System.setProperty(useSystemProxies, String.valueOf(true));
                break;
            case 2:
                System.getProperties().remove(useSystemProxies);
                final String[] manualProxyText = manualProxy.getText().split(":");
                if (manualProxyText.length == 2) {
                    final String host = manualProxyText[0];
                    try {
                        final Integer port = Integer.parseInt(manualProxyText[1]);
                        if (port > 0) {
                            System.setProperty(httpProxyHost, host);
                            System.setProperty(httpProxyPort, String.valueOf(port));
                            System.setProperty(httpsProxyHost, host);
                            System.setProperty(httpsProxyPort, String.valueOf(port));
                        }
                    } catch (NumberFormatException e) {
                        //Unable to configure manual proxy
                    }
                }
                break;
            default:
                break;
        }
    }
}
