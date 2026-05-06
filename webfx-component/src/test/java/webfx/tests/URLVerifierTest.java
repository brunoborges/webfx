package webfx.tests;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import webfx.URLVerifier;

/**
 * Tests for URLVerifier using a local embedded HTTP server to avoid
 * dependency on external network resources.
 */
public class URLVerifierTest {

    private static HttpServer server;
    private static int port;

    @BeforeClass
    public static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        // HTML pages under /browse/ (testHTMLUrls_Test1 and testHTMLUrls_Test2)
        server.createContext("/browse/", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        // /samples redirects to /samples/index.html (testHTMLUrls_Test3)
        server.createContext("/samples", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if ("/samples".equals(path)) {
                exchange.getResponseHeaders().set("Location",
                        "http://localhost:" + port + "/samples/index.html");
                exchange.sendResponseHeaders(302, -1);
            } else {
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, 0);
            }
            exchange.getResponseBody().close();
        });

        // FXML file (testHTMLUrls_Test4)
        server.createContext("/login/login.fxml", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        // Plain-text file (testHTMLUrls_Test5)
        server.createContext("/docs/README.adoc", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        server.start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static String base() {
        return "http://localhost:" + port;
    }

    // These two tests exercise pure URL-string parsing in verifyURL(); no network
    // connection is made because URLVerifier.verifyURL() only constructs a URL object.
    @Test
    public void testVerifyURL_withoutScheme() throws MalformedURLException {
        URL url = URLVerifier.verifyURL("example.com/path");
        assertEquals("http://example.com/path", url.toString());
    }

    @Test
    public void testVerifyURL_withScheme() throws MalformedURLException {
        URL url = URLVerifier.verifyURL("https://example.com/path");
        assertEquals("https://example.com/path", url.toString());
    }

    @Test
    public void testHTMLUrls_Test1() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(base() + "/browse/webfx");
        assertTrue("Final location: " + url0.getLocation(),
                url0.getLocation().toString().startsWith(base() + "/browse/webfx"));
        assertEquals("Base path", new URL(base() + "/browse/"), url0.getBasePath());
        assertEquals("Content Type", "text/html", url0.getContentType().orElse(null));
        assertEquals("Page name", "webfx", url0.getPageName().orElse(null));
        assertEquals("File Extension", "", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test2() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(base() + "/browse/webfx/");
        assertTrue("Final location: " + url0.getLocation(),
                url0.getLocation().toString().startsWith(base() + "/browse/webfx/"));
        assertEquals("Base path", new URL(base() + "/browse/webfx/"), url0.getBasePath());
        assertEquals("Content Type", "text/html", url0.getContentType().orElse(null));
        assertEquals("Page name", "", url0.getPageName().orElse(null));
        assertEquals("File Extension", "", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test3() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(base() + "/samples");
        assertTrue("Final location: " + url0.getLocation(),
                url0.getLocation().toString().startsWith(base() + "/samples/index.html"));
        assertEquals("Base path", new URL(base() + "/samples/"), url0.getBasePath());
        assertEquals("Content Type", "text/html", url0.getContentType().orElse(null));
        assertEquals("Page name", "index", url0.getPageName().orElse(null));
        assertEquals("File Extension", "html", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test4() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(base() + "/login/login.fxml");
        assertTrue("Final location: " + url0.getLocation(),
                url0.getLocation().toString().startsWith(base() + "/login/login.fxml"));
        assertEquals("Base path", new URL(base() + "/login/"), url0.getBasePath());
        assertEquals("Content Type", "application/octet-stream", url0.getContentType().orElse(null));
        assertEquals("Page name", "login", url0.getPageName().orElse(null));
        assertEquals("File Extension", "fxml", url0.getFileExtension().orElse(null));
    }

    @Test
    public void testHTMLUrls_Test5() throws MalformedURLException {
        URLVerifier url0 = new URLVerifier(base() + "/docs/README.adoc");
        assertTrue("Final location: " + url0.getLocation(),
                url0.getLocation().toString().startsWith(base() + "/docs/README.adoc"));
        assertEquals("Base path", new URL(base() + "/docs/"), url0.getBasePath());
        assertEquals("Content Type", "text/plain", url0.getContentType().orElse(null));
        assertEquals("Page name", "README", url0.getPageName().orElse(null));
        assertEquals("File Extension", "adoc", url0.getFileExtension().orElse(null));
    }

}
