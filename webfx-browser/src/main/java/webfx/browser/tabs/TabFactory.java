/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
 * @author Bruno Borges <bruno.borges at oracle.com>
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
