/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import com.webfx.NavigationContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;

/**
 *
 * @author bruno
 */
public interface BrowserTab {

    public ObjectProperty<Node> contentProperty();

    public ReadOnlyStringProperty titleProperty();

    public ReadOnlyStringProperty locationProperty();

    public void stop();
    
    public boolean isStoppable();

    public void setTabManager(TabManager tm);

    public NavigationContext getNavigationContext();

}
