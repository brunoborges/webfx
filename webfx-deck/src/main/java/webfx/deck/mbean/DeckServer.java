package webfx.deck.mbean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import webfx.WebFXRegion;

/**
 *
 * @author bruno
 */
public class DeckServer implements DeckServerMBean {

    private final WebFXRegion webfx;

    public DeckServer(WebFXRegion fxView) {
        this.webfx = fxView;
    }

    @Override
    public void reload() {
        Platform.runLater(webfx::load);
    }

    @Override
    public void load(String url) {
        Platform.runLater(() -> {
            try {
                webfx.getNavigationContext().goTo(new URL(url));
            } catch (MalformedURLException ex) {
                Logger.getLogger(DeckServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
