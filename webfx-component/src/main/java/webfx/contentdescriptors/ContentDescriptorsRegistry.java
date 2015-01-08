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
