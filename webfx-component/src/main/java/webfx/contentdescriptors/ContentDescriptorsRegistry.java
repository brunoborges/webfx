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
package webfx.contentdescriptors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bruno Borges <bruno.borges at oracle.com>
 * @author Nikita Lipsky
 */
public class ContentDescriptorsRegistry {

    private static final Map<String, ContentDescriptor> descriptorsByFileExtension = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, ContentDescriptor> descriptorsByContentType = Collections.synchronizedMap(new HashMap<>());

    static {
        ContentDescriptor.HTML.register();
        ContentDescriptor.FXML.register();
        ContentDescriptor.AsciiDoc.register();
    }

    public static void registerContentDescriptor(ContentDescriptor contentDescriptor, String[] fileExtensions, String[] contentTypes) {
        if (contentDescriptor == null) {
            throw new IllegalArgumentException("ContentDescriptor cannot be null");
        }

        String existentExts = Arrays.stream(fileExtensions).filter(descriptorsByFileExtension.keySet()::contains).collect(Collectors.joining());
        if (existentExts != null && existentExts.isEmpty() == false) {
            throw new IllegalArgumentException("The following extension(s) is/are already registered by another implementation: " + existentExts);
        }

        String existentMimes = Arrays.stream(contentTypes).filter(descriptorsByContentType.keySet()::contains).collect(Collectors.joining());
        if (existentMimes != null && existentMimes.isEmpty() == false) {
            throw new IllegalArgumentException("The following content descriptor(s) is/are already registered by another implementation: " + existentMimes);
        }

        Arrays.stream(fileExtensions).distinct().forEach(ext -> descriptorsByFileExtension.put(ext, contentDescriptor));
        Arrays.stream(contentTypes).distinct().forEach(mime -> descriptorsByContentType.put(mime, contentDescriptor));
    }

    public static ContentDescriptor getContentDescriptor(String fileExtension, String contentTypeStr) {
        ContentDescriptor contentDescriptor = descriptorsByFileExtension.get(fileExtension);
        if (contentDescriptor == null) {
            contentDescriptor = descriptorsByContentType.get(contentTypeStr);
        }

        return contentDescriptor;
    }

}
