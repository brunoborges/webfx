/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.browser.tabs;

import java.net.URL;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;
import webfx.NavigationContext;
import webfx.browser.BrowserTab;
import webfx.browser.TabManager;
import webfx.contentdescriptors.ContentDescriptor;

/**
 *
 * @author bruno
 */
class AsciiDocTab extends BrowserTab {

    public static void register() {
        TabFactory.registerProvider(AsciiDocTab::new, ContentDescriptor.AsciiDoc.instance());
    }

    public AsciiDocTab(TabManager tabManager, Locale locale) {
        super(tabManager);
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyStringProperty locationProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

    @Override
    public NavigationContext getNavigationContext() {
        return new NavigationContext.DefaultNavigationContext() {

            @Override
            public void goTo(String url) {
                super.goTo(url); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void goTo(URL url) {
                super.goTo(url); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }

    @Override
    public ContentDescriptor getContentDescripor() {
        return ContentDescriptor.AsciiDoc.instance();
    }

}
