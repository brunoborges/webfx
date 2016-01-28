package sample.browser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import webfx.applet.JSObjectFX;

/**
 * Sample of a JavaFX browser configured to support execution of 'DataSummaryApplet'.
 * 
 * @author Bruno Borges 
 */
public class Main extends Application {

    private static final String APPLET_PAGE = "http://localhost:8080/applet/launch.html";

    private final WebView webView = new WebView();

    @Override
    public void start(Stage primaryStage) {
        webView.getEngine().load(APPLET_PAGE);

        // Initialize the Applet Support to this webview
        JSObjectFX.enableAppletSupport(webView);

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("JavaFX Applet Sample - " + APPLET_PAGE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
