/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author bruno
 */
public class WebFX extends Application {

    private Logger LOGGER = Logger.getLogger(WebFX.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = getCurrentLocale();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("browser.fxml"), ResourceBundle.getBundle("webfx/browser", locale));
        Parent root = (Parent) fxmlLoader.load();

        BrowserFXController controller = fxmlLoader.getController();
        controller.setLocale(locale);

        Scene scene = new Scene(root);
        
        BrowserShortcuts shortcuts = new BrowserShortcuts(scene);
        shortcuts.setup(controller);

        stage.setTitle("WebFX");
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
