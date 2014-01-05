/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx.tests;

import com.webfx.WebFXRegion;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author bruno
 */
public class NavigationTest extends Application {

    @Override
    public void start(Stage stage) {
        WebFXRegion webfx = new WebFXRegion(getClass().getResource("page-a.fxml"));

        Scene scene = new Scene(webfx, 320, 370);

        stage.setTitle("NavigationTest");
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
