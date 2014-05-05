package webfx.deck.mbean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        webfx.load();
    }

    @Override
    public void load(String url) {
        try {
            webfx.loadUrl(new URL(url));
        } catch (MalformedURLException ex) {
            Logger.getLogger(DeckServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
