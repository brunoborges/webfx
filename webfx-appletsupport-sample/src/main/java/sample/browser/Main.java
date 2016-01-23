package sample.browser;

import sample.applet.DataSummary;
import sample.applet.DataSummaryProvider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import webfx.applet.JSObjectWrapper;
import webfx.applet.JSObjectWrapper.JSObjectWrapperMapper;

/**
 * Sample of a JavaFX browser configured to support execution of 'DataSummaryApplet'.
 * 
 * @author Bruno Borges 
 */
public class Main extends Application implements DataSummaryProvider {

    private static final String APPLET_PAGE = "http://localhost:8080/applet/launch.html";

    private final WebView webView = new WebView();

    @Override
    public void start(Stage primaryStage) {
        webView.getEngine().load(APPLET_PAGE);

        // Register a known applet from the page to JSObjectWrapperMapper
        // Advanced implementation may be able to load classes more dynamically
        JSObjectWrapperMapper mapper = JSObjectWrapper
                .register(DataSummaryProvider.class, this);

        // Initialize the Applet
        JSObjectWrapper.initAppletsForFX(webView, mapper);

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

    @Override
    public DataSummary getDataSummary() {
        return new DataSummary(JSObjectWrapper.getWindow(webView.getEngine()));
    }

}
