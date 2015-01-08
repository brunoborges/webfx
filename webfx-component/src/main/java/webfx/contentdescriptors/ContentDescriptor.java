package webfx.contentdescriptors;

/**
 * Describes the content to be rendered by a tab.
 *
 * @author Nikita Lipsky
 */
public interface ContentDescriptor {

    String[] getFileExtensions();

    String[] getContentTypes();

    /**
     * HTML content descriptor singleton.
     */
    public class HTML implements ContentDescriptor {
        private HTML(){}

        private static final HTML INSTANCE = new HTML();
        private static final String[] CONTENT_TYPES = new String[]{"text/html", "text/xhtml"};
        private static final String[] FILE_EXTENSIONS = new String[]{"htm", "html", "xhtml", "shtml"};

        @Override
        public String[] getFileExtensions() {
            return FILE_EXTENSIONS;
        }

        @Override
        public String[] getContentTypes() {
            return CONTENT_TYPES;
        }

        public static ContentDescriptor instance() {
            return INSTANCE;
        }

        public static void register() {
            ContentDescriptorsRegistry.registerContentDescriptor(INSTANCE, FILE_EXTENSIONS, CONTENT_TYPES);
        }
    }

    /**
     * FXML content descriptor singleton.
     */
    public class FXML implements ContentDescriptor {
        private FXML(){}
        private static final FXML INSTANCE = new FXML();
        private static final String[] CONTENT_TYPES = {"text/x-fxml+xml", "text/x-fxml", "application/fxml", "application/xml"};
        private static final String[] FILE_EXTENSIONS = {"fxml"};

        @Override
        public String[] getFileExtensions() {
            return FILE_EXTENSIONS;
        }

        @Override
        public String[] getContentTypes() {
            return CONTENT_TYPES;
        }

        public static ContentDescriptor instance() {
            return INSTANCE;
        }

        public static void register() {
            ContentDescriptorsRegistry.registerContentDescriptor(INSTANCE, FILE_EXTENSIONS, CONTENT_TYPES);
        }
    }

    /**
     * AsciiDoc content descriptor singleton.
     */
    public class AsciiDoc implements ContentDescriptor {
        private AsciiDoc(){}
        private static final AsciiDoc INSTANCE = new AsciiDoc();
        private static final String[] CONTENT_TYPES = new String[]{"text/x-asciidoc", "text/asciidoc", "application/asciidoc"};
        private static final String[] FILE_EXTENSIONS = new String[]{"asciidoc", "adoc"};

        @Override
        public String[] getFileExtensions() {
            return FILE_EXTENSIONS;
        }

        @Override
        public String[] getContentTypes() {
            return CONTENT_TYPES;
        }

        public static ContentDescriptor instance() {
            return INSTANCE;
        }

        public static void register() {
            ContentDescriptorsRegistry.registerContentDescriptor(INSTANCE, FILE_EXTENSIONS, CONTENT_TYPES);
        }
    }

    /**
     * No content descriptor singleton.
     */
    public class NoContent implements ContentDescriptor {
        private NoContent(){}
        private static final NoContent INSTANCE = new NoContent();

        @Override
        public String[] getFileExtensions() {
            return null;
        }

        @Override
        public String[] getContentTypes() {
            return null;
        }

        public static ContentDescriptor instance() {
            return INSTANCE;
        }

    }

}
