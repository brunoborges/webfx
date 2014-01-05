/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.webfx.WebFXView;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public class RemoteURLTest extends Application {

    @Override
    public void start(Stage stage) {
        WebFXView webfx = null;
        try {
            webfx = new WebFXView(new URL("http://localhost:8080/webfx-samples/jdk8/metronome/metronome.fxml"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(RemoteURLTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(webfx, 320, 370);

        stage.setTitle("Hello World!");
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
        launch(args);
    }

}
