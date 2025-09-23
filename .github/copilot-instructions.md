# WebFX - JavaFX Web Browser and FXML Pages

WebFX is a JavaFX-based web browser that can render both standard HTML and FXML-based web pages. It includes a browser application, a deck viewer for single FXML pages, and sample applications demonstrating the technology.

**CRITICAL**: Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Environment Setup
- Java 17+ is required (OpenJDK 17.0.16+ verified working)
- Maven 3.9+ is required (Apache Maven 3.9.11+ verified working)
- No additional SDK downloads required - Maven handles all JavaFX dependencies automatically

### Bootstrap, Build, and Test the Repository
Execute these commands in sequence from the repository root:

```bash
cd /path/to/webfx
mvn clean install -Dmaven.test.skip=true
```
**NEVER CANCEL**: Build takes 15-20 seconds. NEVER CANCEL. Set timeout to 120+ seconds minimum.

**Build Output**: Successful build creates JARs in:
- `webfx-component/target/webfx-component-1.0-SNAPSHOT.jar`
- `webfx-deck/target/webfx-deck-1.0-SNAPSHOT.jar` 
- `webfx-browser/target/webfx-browser-1.0-SNAPSHOT.jar`

### Running Tests
```bash
mvn test
```
**NEVER CANCEL**: Tests take 5-10 seconds but FAIL due to network connectivity issues (external URL tests). NEVER CANCEL. Set timeout to 60+ seconds minimum.

**Expected Test Behavior**: URLVerifierTest fails due to external network dependencies (github.com, learnjavafx.typepad.com). This is expected in sandboxed environments and does NOT indicate build problems.

### Running Applications

#### WebFX Browser Application
The browser must be run with proper JavaFX module path setup:
```bash
cd webfx-browser
mvn exec:java
```
**Note**: Requires X11 display. In headless environments, the browser will fail to start (this is expected).

#### WebFX Deck Application  
Run the deck with a remote FXML URL:
```bash
cd webfx-deck
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=- | tail -1) webfx.deck.DeckMain -Dwebfx.url=http://localhost:8080/webfx-samples/login/login.fxml
```

#### WebFX Samples Web Server
Start the samples server (requires fixing Jetty version first):
```bash
cd webfx-samples
mvn jetty:run
```
**Known Issue**: Jetty plugin version conflict causes server startup failure. Server starts on port 8080 but exits immediately.

## Validation

### Manual Testing Requirements
**CRITICAL**: After making any code changes, ALWAYS run through these validation scenarios:

1. **Build Validation**:
   ```bash
   mvn clean install -Dmaven.test.skip=true
   ```
   Verify all 4 modules compile successfully.

2. **Component Integration Test**:
   ```bash
   mvn test -Dtest=URLVerifierTest -Dmaven.test.failure.ignore=true
   ```
   Verify test infrastructure works (failures due to network are acceptable).

3. **JAR Generation Validation**:
   Confirm these files are created with reasonable sizes:
   - `webfx-browser/target/webfx-browser-1.0-SNAPSHOT.jar` (~874KB)
   - `webfx-component/target/webfx-component-1.0-SNAPSHOT.jar` (~39KB)  
   - `webfx-deck/target/webfx-deck-1.0-SNAPSHOT.jar` (~7KB)

### Build and CI Requirements
- Always run `mvn clean install -Dmaven.test.skip=true` before committing changes
- Tests are expected to fail in CI due to external network dependencies
- No linting or formatting tools are configured - follow existing Java code style

## Project Structure and Key Components

### Main Modules
- **webfx-parent**: Parent POM with common configuration
- **webfx-component**: Core JavaFX WebView component with FXML loading capabilities  
- **webfx-browser**: Full browser application with navigation and bookmarks
- **webfx-deck**: Minimal viewer for single FXML pages (ideal for embedded devices)
- **webfx-samples**: Sample FXML applications and web content

### Key Technologies
- JavaFX 11-ea+25 with WebView component
- Groovy JSR-223 for scripting support
- Jersey client for REST communication
- JRuby integration for Ruby scripting
- Maven build system with dependency unpacking

### Important Files and Locations
```
webfx/
├── pom.xml                    # Parent POM with JavaFX dependencies
├── webfx-browser/
│   ├── src/main/java/webfx/browser/WebFX.java        # Main browser class
│   └── src/main/resources/                           # Browser resources (172 files)
├── webfx-component/
│   ├── src/main/java/webfx/                         # Core WebFX components
│   └── src/test/java/webfx/tests/URLVerifierTest.java # Network tests (fail in CI)
├── webfx-deck/
│   └── src/main/java/webfx/deck/DeckMain.java       # Single-page viewer
└── webfx-samples/
    └── src/main/webapp/                              # Sample FXML applications
```

## Common Tasks

### Building from Scratch
```bash
git clone <repo-url>
cd webfx
mvn clean install -Dmaven.test.skip=true
```
**Time**: 15-20 seconds for clean build

### Incremental Build
```bash
mvn compile
```
**Time**: 1-3 seconds for incremental compilation

### Running Individual Modules
```bash
# Browser (requires display)
cd webfx-browser && mvn exec:java

# Component tests (expected failures)  
cd webfx-component && mvn test
```

## Troubleshooting

### Common Build Issues
- **JavaFX Platform Detection**: Build automatically detects Linux platform and downloads appropriate JavaFX binaries
- **Missing webfx-component**: Run `mvn install` from root to install dependencies to local repository
- **Jetty Version Conflict**: webfx-samples Jetty plugin has version compatibility issues with current Maven
- **Test Failures**: URLVerifierTest failures due to external network connectivity are normal and expected

### Expected Error Messages  
- `UnknownHostException: learnjavafx.typepad.com` - Network test failure (normal)
- `no main manifest attribute in webfx-browser-*.jar` - Use `mvn exec:java` instead of `java -jar`
- `BUILD FAILURE` on `mvn test` - Network-dependent tests fail (use `-Dmaven.test.skip=true`)

### Performance Notes
- Initial build downloads ~50MB of JavaFX and Maven dependencies
- Subsequent builds are much faster (1-3 seconds)
- JavaFX applications require X11 display to run

## Common File System Locations

### Repository root directory listing
```
ls /path/to/webfx
.git                             # Git repository data
.github                          # GitHub configuration and instructions
.gitignore                      # Git ignore patterns  
.idea                           # IntelliJ IDEA project files
LICENSE.txt                     # Project license
README.md                       # Main project documentation
browser-applet-1.png           # Architecture diagram
browser-applet-2.png           # Browser concept diagram  
hybrid-javafx-architecture.png # Hybrid application architecture
pom.xml                        # Parent Maven POM
webfx-appletsupport-sample/    # Applet migration proof of concept
webfx-browser/                 # Full browser application  
webfx-component/               # Core WebFX components
webfx-deck/                    # Single-page FXML viewer
webfx-parent.iml               # IntelliJ module file
webfx-samples/                 # Sample FXML applications
```

### Sample FXML applications 
```
ls webfx-samples/src/main/webapp/
WEB-INF/         # Web application configuration
calendar/        # Calendar gauge sample (JFXtras)
hellogroovy/     # Groovy scripting example
helloworld/      # Basic HelloWorld FXML
index.html       # Sample application index page
login/           # Login form demonstration
media/           # Media player sample
metronome/       # Metronome application
restdemo/        # REST API demonstration
tableview/       # TableView component example
```

### Browser source structure
```
ls webfx-browser/src/main/java/webfx/browser/
BrowserFXController.java  # Main browser window controller
BrowserShortcuts.java     # Keyboard shortcuts handler
BrowserTab.java           # Individual tab management
TabManager.java           # Multiple tab coordination
WebFX.java               # Main application entry point
settings/                # Browser settings management
tabs/                    # Tab-related components
util/                    # Browser utility classes
```