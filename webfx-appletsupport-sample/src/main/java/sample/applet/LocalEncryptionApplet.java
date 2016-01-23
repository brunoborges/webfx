package sample.applet;

import java.applet.Applet;
import java.util.Base64;

/**
 * Dummy applet to mock encryption on client-side. Uses Base64 as example.
 *
 * Shows how Javascript can call this object either on web browsers (NPAPI) or
 * using a JavaFX-based browser.
 *
 * @author Bruno Borges
 */
public class LocalEncryptionApplet extends Applet {

    public String encryptText(String text) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(text.getBytes());
    }

}
