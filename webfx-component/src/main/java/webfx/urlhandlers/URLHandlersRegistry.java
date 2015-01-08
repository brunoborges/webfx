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
package webfx.urlhandlers;

import webfx.URLVerifier;
import webfx.contentdescriptors.ContentDescriptorsRegistry;

import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The registry of URL handlers.
 * There is a predefined URL handler for usual protocols like {@code http://}.
 * You may define your own URL handlers via {@code -Dwebfx.url.handlers=handler1,handler2}
 * VM property that defines the list of classes of URL handlers to register in the registry.
 *
 * See https://github.com/pjBooms/Java-ReStart/javarestart-webfx for
 * {@code java://} and {wfx://} protocols implementations, for example.
 *
 * @author Nikita Lipsky
 */
public class URLHandlersRegistry {

    private static final Logger LOGGER = Logger.getLogger(URLHandlersRegistry.class.getName());

    private static HashMap<String, URLHandler> handlers = new HashMap<>();

    private static void registerURLHandler(URLHandler handler) {
        for (String protocol: handler.getProtocols()) {
            handlers.put(protocol, handler);
        }
    }

    static {
        registerURLHandler(new URLHandler() {
            @Override
            public String[] getProtocols() {
                return new String[]{"http", "https", "file"};
            }

            @Override
            public Result handle(URL url) {
                URLVerifier urlVerifier = new URLVerifier(url);
                String fileExtension = urlVerifier.getFileExtension().orElse(null);
                String contentType = urlVerifier.getContentType().orElse(null);
                return new Result(ContentDescriptorsRegistry.getContentDescriptor(fileExtension, contentType), null);
            }
        });

        String urlHandlers = System.getProperty("webfx.url.handlers");
        if (urlHandlers != null) {
            for (String handler: urlHandlers.split(",")) {
                try {
                    Class handlerClass = Class.forName(handler);
                    registerURLHandler((URLHandler) handlerClass.newInstance());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Invalid URL handler specified: " + handler, e);
                }
            }
        }
    }


    public static URLHandler getHandler(URL url) {
        return handlers.get(url.getProtocol());
    }
}
