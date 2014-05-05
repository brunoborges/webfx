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
package webfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public class ResourceBundleLoader {

    private static final Logger LOGGER = Logger.getLogger(ResourceBundleLoader.class.getName());

    private final Locale locale;
    private final PageContext pageContext;

    public ResourceBundleLoader(PageContext pageContext, Locale locale) {
        this.pageContext = pageContext;
        this.locale = locale == null ? Locale.getDefault() : locale;
    }

    public ResourceBundleLoader(PageContext pageContext) {
        this(pageContext, Locale.getDefault());
    }

    protected ResourceBundle findBundle() {
        ResourceBundle found = null;

        Iterable<String> bundleNames = constructBundleFileNames();

        URL baseURL = pageContext.getBasePath();

        for (String bundleName : bundleNames) {
            try {
                URL urlBundle = new URL(baseURL.toString() + "/" + bundleName);

                try (InputStream bundleIS = urlBundle.openStream()) {
                    found = new PropertyResourceBundle(bundleIS);
                    LOGGER.log(Level.INFO, "Bundle found: {0}", bundleName);
                    break;
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Bundle not found: {0}", bundleName);
                    LOGGER.log(Level.FINEST, "Bundle not found: " + bundleName, ex);
                }
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return found;
    }

    private Iterable<String> constructBundleFileNames() {
        String filename = pageContext.getPageName();

        List<String> names = new ArrayList<>();
        String l0 = new Locale(locale.getLanguage(), locale.getCountry()).toString();
        String l1 = new Locale(locale.getLanguage()).toString();
        names.add(filename + "_" + l0 + ".properties");
        names.add(filename + "_" + l1 + ".properties");
        names.add(filename + ".properties");

        return Collections.unmodifiableList(names);
    }
}
