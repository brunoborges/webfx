/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.URL;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;

/**
 *
 * @author bruno
 */
public interface BrowserTab {
    
    public ObjectProperty<Node> contentProperty();
    
    public void forward();
    
    public void back();
    
    public void goTo(URL url);
    
    public void goTo(URL url, Locale locale);
    
    public ReadOnlyStringProperty titleProperty();
    
    public ReadOnlyStringProperty locationProperty();

    public void reload();

    public void stop();
    
}
