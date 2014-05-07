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
