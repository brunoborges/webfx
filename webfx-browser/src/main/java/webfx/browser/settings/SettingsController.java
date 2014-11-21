/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        if(check.isSelected()){
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
