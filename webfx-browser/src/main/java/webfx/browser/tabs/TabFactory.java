/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.browser.tabs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import webfx.browser.BrowserTab;
import webfx.browser.TabManager;
import webfx.contentdescriptors.ContentDescriptor;

/**
 *
 * @author bruno
 */
public final class TabFactory {

    private static final Map<ContentDescriptor, TabConstructor> providersByContentDescriptor = Collections.synchronizedMap(new HashMap<>());

    static {
        HTMLTab.register();
        FXTab.register();
        AsciiDocTab.register();
    }

    private TabFactory() {
    }

    public static void registerProvider(TabConstructor tabConstructor, ContentDescriptor contentDescriptor) {
        if (tabConstructor == null) {
            throw new IllegalArgumentException("TabManager cannot be null");
        }

        if (contentDescriptor == null) {
            throw new IllegalArgumentException("ContentDescriptor cannot be null");
        }

        providersByContentDescriptor.put(contentDescriptor, tabConstructor);

    }

    public static BrowserTab newTab(TabManager tabManager, Locale locale, ContentDescriptor contentDescriptor) {
        if (tabManager == null) {
            throw new IllegalArgumentException("TabManager cannot be null");
        }

        TabConstructor tabConstructor = providersByContentDescriptor.get(contentDescriptor);
        if (tabConstructor == null) {
            throw new IllegalArgumentException(String.format("Didn't find a tab provider for contentDescriptor [%s]", contentDescriptor));
        }

        return tabConstructor.newBrowserTab(tabManager, locale);
    }

    @FunctionalInterface
    public static interface TabConstructor {

        public BrowserTab newBrowserTab(TabManager tabManager, Locale locale);

    }

}
