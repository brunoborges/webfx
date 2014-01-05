/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import com.webfx.NavigationContext;
import com.webfx.WebFXRegion;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public class FXTab implements BrowserTab {

    private final ReadOnlyStringWrapper locationProperty = new ReadOnlyStringWrapper();
    private final SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();
    private final WebFXRegion webfx;
    private TabManager tabManager;

    public FXTab() {
        webfx = new WebFXRegion();
        contentProperty.set(webfx);
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return webfx.getCurrentViewTitleProperty();
    }

    @Override
    public ReadOnlyStringProperty locationProperty() {
        return locationProperty;
    }

    @Override
    public void stop() {
        contentProperty.set(null);
    }

    @Override
    public void setTabManager(TabManager tm) {
        this.tabManager = tm;
    }

    @Override
    public NavigationContext getNavigationContext() {
        return webfx.getNavigationContext();
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

}
