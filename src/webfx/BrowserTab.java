/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.URL;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import webfx.context.NavigationContext;

/**
 *
 * @author bruno
 */
public interface BrowserTab {

    public ObjectProperty<Node> contentProperty();

    public ReadOnlyStringProperty titleProperty();

    public ReadOnlyStringProperty locationProperty();

    public void reload();

    public void stop();

    public void setTabManager(TabManager tm);

    public NavigationContext getNavigationContext();

    public void forward();

    public void back();

    public void goTo(URL url);

    public void goTo(URL url, Locale locale);

    public ObservableList<HistoryEntry> getHistory();
}
