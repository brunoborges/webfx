/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.context;

import java.net.URL;
import java.util.Locale;

/**
 *
 * @author bruno
 */
public interface NavigationContext {
    
    public void forward();
    
    public void back();
    
    public void goTo(String url);

}
