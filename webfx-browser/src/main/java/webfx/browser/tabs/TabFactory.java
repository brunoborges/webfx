/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.browser.tabs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import webfx.browser.BrowserTab;
import webfx.browser.TabManager;

/**
 *
 * @author bruno
 */
public final class TabFactory {

    private static final Map<String, TabConstructor> providersByFileExtension = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, TabConstructor> providersByContentType = Collections.synchronizedMap(new HashMap<>());

    static {
        HTMLTab.register();
        FXTab.register();
        AsciiDocTab.register();
    }

    private TabFactory() {
    }

    public static void registerProvider(TabConstructor tabConstructor, String[] fileExtensions, String[] contentTypes) {
        if (tabConstructor == null) {
            throw new IllegalArgumentException("TabManager cannot be null");
        }

        String existentExts = Arrays.stream(fileExtensions).filter(providersByFileExtension.keySet()::contains).collect(Collectors.joining());
        if (existentExts != null && existentExts.isEmpty() == false) {
            throw new IllegalArgumentException("The following extension(s) is/are already registered by another implementation: " + existentExts);
        }

        String existentMimes = Arrays.stream(contentTypes).filter(providersByContentType.keySet()::contains).collect(Collectors.joining());
        if (existentMimes != null && existentMimes.isEmpty() == false) {
            throw new IllegalArgumentException("The following content type(s) is/are already registered by another implementation: " + existentMimes);
        }

        Arrays.stream(fileExtensions).distinct().forEach(ext -> providersByFileExtension.put(ext, tabConstructor));
        Arrays.stream(contentTypes).distinct().forEach(mime -> providersByContentType.put(mime, tabConstructor));
    }

    public static BrowserTab newTab(TabManager tabManager, Locale locale, String fileExtension, String contentType) {
        if (tabManager == null) {
            throw new IllegalArgumentException("TabManager cannot be null");
        }

        TabConstructor tabConstructor = providersByFileExtension.get(fileExtension);
        if (tabConstructor == null) {
            tabConstructor = providersByContentType.get(contentType);
        }

        if (tabConstructor == null) {
            throw new IllegalArgumentException(String.format("Didn't find a tab provider for fileExtension [%s] nor contentType [%s]", fileExtension, contentType));
        }

        return tabConstructor.newBrowserTab(tabManager, locale);
    }

    @FunctionalInterface
    public static interface TabConstructor {

        public BrowserTab newBrowserTab(TabManager tabManager, Locale locale);

    }

}
