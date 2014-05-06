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

    private WebFXRegion fxView;

    @Override
    public void start(Stage primaryStage) {
        try {
            String url = System.getProperty("webfx.url");
            // url = "http://10.42.0.1:8080/webfx-samples/login/login.fxml";
            if (url == null) {
                throw new IllegalArgumentException("-Dwebfx.url must be provided!");
            }
            fxView = new WebFXRegion(new URL(url));

            startMBeanServer(fxView);

            StackPane root = new StackPane();
            root.getChildren().add(fxView);

            Scene scene = new Scene(root, 1280, 800);
            fxView.setOnKeyPressed(e -> {
                KeyCode keyCode = e.getCode();
                if (keyCode.equals(KeyCode.F5) || (keyCode.equals(KeyCode.R) && e.isControlDown())) {
                    reload();
                }

                if (keyCode.equals(KeyCode.LEFT) && e.isControlDown()) {
                    goBack();
                }

                if (keyCode.equals(keyCode.RIGHT) && e.isControlDown()) {
                    goForward();
                }

                if (keyCode.equals(keyCode.Q) && e.isControlDown()) {
                    System.exit(0);
                }
            });

            fxView.getCurrentViewTitleProperty().addListener((a, b, c) -> {
                primaryStage.setTitle(c);
            });
            primaryStage.setScene(scene);
            if (System.getProperty("os.arch").toUpperCase().contains("ARM")) {
                root.setPrefSize(1280, 800);
                //root.setScaleY(0.9);
                //root.setTranslateY();

                primaryStage.setFullScreen(true);
                primaryStage.setFullScreenExitHint("");
            }

            primaryStage.show();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DeckMain.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DeckMain.class.getName()).log(Level.SEVERE, null, ex);
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
