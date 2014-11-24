/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import webfx.NavigationContext;
import webfx.WebFXRegion;
import webfx.browser.BrowserTab;
import webfx.browser.TabManager;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
class FXTab extends BrowserTab {

    private final ReadOnlyStringWrapper locationProperty = new ReadOnlyStringWrapper();
    private final SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();
    private final WebFXRegion webfx;

    private static final String[] CONTENT_TYPES = {"text/x-fxml+xml", "text/x-fxml", "application/fxml", "application/xml"};
    private static final String[] FILE_EXTENSIONS = {"fxml"};

    public static void register() {
        TabFactory.registerProvider(FXTab::new, FILE_EXTENSIONS, CONTENT_TYPES);
    }

    public FXTab(TabManager tabManager, Locale locale) {
        super(tabManager);
        webfx = new WebFXRegion();
        contentProperty.set(webfx);
        webfx.setLocale(locale);
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
    public NavigationContext getNavigationContext() {
        return webfx.getNavigationContext();
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

    @Override
    public String[] getFileExtensions() {
        return CONTENT_TYPES;
    }

    @Override
    public String[] getContentTypes() {
        return FILE_EXTENSIONS;
    }

}
