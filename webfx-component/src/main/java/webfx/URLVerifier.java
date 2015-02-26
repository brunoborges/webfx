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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import webfx.contentdescriptors.ContentDescriptor;
import webfx.contentdescriptors.ContentDescriptorsRegistry;

/**
 *
 * @author bruno
 */
public class URLVerifier {

    private static CloseableHttpClient httpclient;

    private URL location;
    private URL basePath;
    private String pageName;
    private String fileExtension;
    private String contentType;

    static {
        httpclient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

    private static final Logger LOGGER = Logger.getLogger(URLVerifier.class.getName());

    public static URL verifyURL(String location) throws MalformedURLException {
        URL url;
        try {
            url = new URL(location);
        } catch (MalformedURLException e) {
            File f = new File(location.trim());
            if (f.isAbsolute() && f.exists()) {
                url = f.toURI().toURL();
            } else {
                url = new URL("http://" + location);
            }
        }
        return url;
    }

    public URLVerifier(String location) throws MalformedURLException {
        this.location = verifyURL(location);
        try {
            discoverThroughHeaders();
            findBasePath();
            findPageNameAndFileExtension();
        } catch (URISyntaxException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public URLVerifier(URL location) {
        this.location = location;

        try {
            discoverThroughHeaders();
            findBasePath();
            findPageNameAndFileExtension();
        } catch (URISyntaxException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void discoverThroughHeaders() throws IOException, URISyntaxException {
        // relax redirections
        HttpGet httpGet = new HttpGet(location.toURI());
        HttpClientContext httpcontext = HttpClientContext.create();
        try (CloseableHttpResponse response = httpclient.execute(httpGet, httpcontext)) {
            // get mimetype via Content-Type http header
            Arrays.stream(response.getHeaders("Content-Type")).findFirst().ifPresent(h -> this.contentType = h.getValue());
            if (!Objects.isNull(contentType)) {
                contentType = contentType.contains(";") ? contentType.substring(0, contentType.indexOf(";")).trim() : contentType;
                LOGGER.log(Level.INFO, "Final Content-Type: {0}", contentType);
            } else {
                LOGGER.log(Level.INFO, "Content-Type Header is Empty: {0}", Arrays.toString(response.getHeaders("Content-Type")));
                // clear field b/c it was used inside lambda as temp var
                contentType = null;
            }

            // get filename via Content-Disposition http header
            Arrays.stream(response.getHeaders("Content-Disposition")).findFirst().ifPresent(h -> this.pageName = h.getValue());
            if (!Objects.isNull(pageName) && pageName.contains("filename=")) {
                pageName = pageName.substring(pageName.lastIndexOf("filename=") + 9);
                LOGGER.log(Level.INFO, "temporary page name: {0}", pageName);
                if (pageName.indexOf('.') > -1) {
                    fileExtension = pageName.substring(pageName.indexOf('.') + 1).trim();
                    LOGGER.log(Level.INFO, "Final file extension: {0}", fileExtension);
                }
                pageName = pageName.substring(0, pageName.indexOf('.')).trim();
                LOGGER.log(Level.INFO, "Final page name: {0}", pageName);
            } else {
                // clear field b/c it was used inside lambda as temp var
                pageName = null;
            }

            HttpHost target = httpcontext.getTargetHost();
            List<URI> redirectLocations = httpcontext.getRedirectLocations();
            URI _loc = URIUtils.resolve(httpGet.getURI(), target, redirectLocations);
            this.location = _loc.toURL();
            LOGGER.log(Level.INFO, "Final HTTP location: {0}", _loc.toURL());
        }
    }

    private void findBasePath() throws URISyntaxException, IOException {
        String path = location.getPath();
        LOGGER.log(Level.INFO, "Location.path: {0}", path);

        int lastSlash = path.lastIndexOf('/');

        if (lastSlash == -1 || "/".equals(path) || path.isEmpty()) {
            basePath = new URL(location.getProtocol(), location.getHost(), location.getPort(), path);
            return;
        }

        path = path.substring(0, lastSlash);
        LOGGER.log(Level.INFO, "Path is now {0}", path);

        try {
            basePath = new URL(location.getProtocol(), location.getHost(), location.getPort(), path + "/");
            LOGGER.log(Level.INFO, "BasePath is now {0}", basePath);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void findPageNameAndFileExtension() {
        String path = location.getPath();
        LOGGER.log(Level.INFO, "Path is {0}", path);

        int lastSlash = path.lastIndexOf('/');

        if (pageName == null) {
            pageName = pageName == null ? location.getPath().substring(lastSlash + 1) : pageName;
            LOGGER.log(Level.INFO, "pageName is now {0}", pageName);
        }

        if (pageName != null && fileExtension == null) {
            int indexOfExtension = pageName.indexOf('.');
            if (indexOfExtension > 0) {
                LOGGER.log(Level.INFO, "pageName has ''.'' char at index {0}", indexOfExtension);
                fileExtension = path.substring(path.lastIndexOf('.') + 1);
                pageName = pageName.substring(0, indexOfExtension);
            }

            if (fileExtension == null) {
                fileExtension = "";
            }
        }
    }

    public URL getBasePath() {
        return basePath;
    }

    public URL getLocation() {
        return location;
    }

    /**
     * @return the pageName
     */
    public Optional<String> getPageName() {
        return Optional.ofNullable(pageName);
    }

    public Optional<String> getFileExtension() {
        return Optional.ofNullable(fileExtension);
    }

    public Optional<String> getContentType() {
        return Optional.ofNullable(contentType);
    }

    public ContentDescriptor getContentDescriptor() {
        return ContentDescriptorsRegistry.getContentDescriptor(fileExtension, contentType);
    }

}
