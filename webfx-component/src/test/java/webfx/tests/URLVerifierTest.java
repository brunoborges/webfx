/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.tests;

import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import webfx.URLVerifier;

/**
 *
 * @author bruno
 */
public class URLVerifierTest {

    @Test
    public void testHTMLUrls_Test1() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier("github.com/brunoborges/webfx");
        assertTrue("Final location: " + url0.getLocation().toString(),
                url0.getLocation().toString().startsWith("https://github.com/brunoborges/webfx"));
        assertEquals("Base path", new URL("https://github.com/brunoborges/"), url0.getBasePath());
        assertEquals("Content Type", "text/html", url0.getContentType().orElse(null));
        assertEquals("Page name", "webfx", url0.getPageName().orElse(null));
        assertEquals("File Extension", "", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test2() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier("github.com/brunoborges/webfx/");
        assertTrue("Final location: " + url0.getLocation().toString(),
                url0.getLocation().toString().startsWith("https://github.com/brunoborges/webfx"));
        assertEquals("Base path", new URL("https://github.com/brunoborges/webfx/"), url0.getBasePath());
        assertEquals("Content Type", "text/html", url0.getContentType().orElse(null));
        assertEquals("Page name", "", url0.getPageName().orElse(null));
        assertEquals("File Extension", "", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test5() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(
                "https://raw.githubusercontent.com/mojavelinux/asciidoctor-servlet-demo/master/README.adoc");
        assertTrue("Final location: " + url0.getLocation().toString(), url0.getLocation().toString().startsWith(
                "https://raw.githubusercontent.com/mojavelinux/asciidoctor-servlet-demo/master/README.adoc"));
        assertEquals("Base path",
                new URL("https://raw.githubusercontent.com/mojavelinux/asciidoctor-servlet-demo/master/"),
                url0.getBasePath());
        assertEquals("Content Type", "text/plain", url0.getContentType().orElse(null));
        assertEquals("Page name", "README", url0.getPageName().orElse(null));
        assertEquals("File Extension", "adoc", url0.getFileExtension().orElse(null));
    }

}
