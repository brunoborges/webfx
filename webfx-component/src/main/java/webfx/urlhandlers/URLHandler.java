package webfx.urlhandlers;

import webfx.contentdescriptors.ContentDescriptor;

import java.net.URL;

/**
 * WebFX URL handlers abstract interface.
 * It is assumed that the browser can handle several protocols
 * and there should be a mechanism to extend supported protocols.
 *
 * @see webfx.urlhandlers.URLHandlersRegistry
 *
 * @author Nikita Lipsky
 */
public interface URLHandler {

    /**
     * Result of {@link #handle}.
     * It is actually tuple of two results: content descriptor and
     * classloader that can be used to load classes referenced by the content to render
     */
    public class Result {
        public final ContentDescriptor contentDescriptor;
        public final ClassLoader classLoader;

        public Result(ContentDescriptor contentDescriptor, ClassLoader classLoader) {
            this.contentDescriptor = contentDescriptor;
            this.classLoader = classLoader;
        }
    }

    /**
     * @return the list of supported protocols by this handler.
     */
    String[] getProtocols();


    /**
     * Handles specified URL.
     *
     */
    Result handle(URL url);

}
