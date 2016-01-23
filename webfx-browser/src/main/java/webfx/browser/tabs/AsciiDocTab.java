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
