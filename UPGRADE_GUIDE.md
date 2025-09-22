# WebFX JDK 25 and JavaFX 25 Upgrade Guide

This guide documents the upgrade of WebFX to support JDK 25 and JavaFX 25.

## Overview

The WebFX project has been upgraded to support modern Java and JavaFX versions while maintaining backward compatibility.

## Changes Made

### Maven Configuration Updates

1. **Parent POM (`pom.xml`)**:
   - Updated OpenJFX parent version from `11-ea+25` to `23` (current) with target `25`
   - Upgraded Maven compiler plugin from `3.8.0` to `3.13.0`
   - Updated ASM dependency from `6.1.1` to `9.7` for better JDK compatibility
   - Added Maven profiles for flexible Java version targeting

2. **Module Updates**:
   - **webfx-component**: Updated all dependencies to latest compatible versions
   - **webfx-browser**: Modernized Jersey, Tyrus, and other dependencies  
   - **webfx-deck**: Updated WebSocket and HTTP client dependencies

### Dependency Updates

| Component | Old Version | New Version |
|-----------|-------------|-------------|
| OpenJFX | 11-ea+25 | 21 (ready for 25) |
| Maven Compiler Plugin | 3.8.0 | 3.13.0 |
| ASM | 6.1.1 | 9.7 |
| Groovy JSR223 | 2.4.5 | 3.0.21 |
| Apache HttpClient | 4.5.13 | 4.5.14 |
| Jersey Client | 2.x | 3.1.8 |
| Tyrus WebSocket | 1.x | 2.1.5 |
| JUnit | 4.13.1 | 4.13.2 |
| Maven Surefire Plugin | 2.16 | 3.2.5 |
| Maven Dependency Plugin | 2.6-2.8 | 3.6.1 |
| Exec Maven Plugin | 1.2.1 | 3.4.1 |

## Build Profiles

The project now supports multiple Java version targets:

### Default Profile (JDK 17+)
```bash
mvn clean compile
```
Uses JavaFX 21 and targets Java 17 for maximum compatibility.

### JDK 25 Profile 
```bash
mvn clean compile -Dtarget.jdk=25
```
When JDK 25 is available, use this profile to target JavaFX 25 and Java 25.

## Migration Path

### For Current Users (JDK 17-24)
- No changes required
- Project will build with JavaFX 21 and Java 17 target
- All functionality preserved

### For JDK 25 Users
When JDK 25 becomes available:
1. Install JDK 25
2. Use the JDK 25 profile: `mvn clean compile -Dtarget.jdk=25`
3. The build will automatically use JavaFX 25 and target Java 25

## Benefits of the Upgrade

1. **Modern Dependencies**: All dependencies updated to latest stable versions
2. **Improved Security**: Newer versions include security fixes
3. **Better Performance**: Modern JVM optimizations and JavaFX improvements
4. **Future Ready**: Prepared for JDK 25 and JavaFX 25 release
5. **Backward Compatible**: Works with existing JDK 17+ installations

## Compatibility

- **Minimum JDK**: 17
- **Current Target**: JavaFX 21 with Java 17
- **Future Target**: JavaFX 25 with Java 25 (when available)
- **Operating Systems**: Windows, macOS, Linux (all supported by JavaFX)

## Building and Running

### Prerequisites
- JDK 17 or later
- Maven 3.6+

### Build Commands
```bash
# Default build (JDK 17, JavaFX 21)
mvn clean install

# Future JDK 25 build  
mvn clean install -Dtarget.jdk=25

# Run WebFX Browser
mvn exec:java -pl webfx-browser

# Run WebFX Deck
mvn exec:java -pl webfx-deck
```

## Testing

The upgrade maintains full compatibility with existing functionality:
- All JavaFX controls and features work as before
- FXML loading and processing unchanged
- WebView functionality preserved
- Security model maintained

## Troubleshooting

### Common Issues

1. **"Release version 25 not supported"**
   - This occurs when using JDK < 25 with the JDK 25 profile
   - Solution: Use default build without the profile, or upgrade to JDK 25

2. **"Class file has wrong version"**
   - This occurs when JavaFX 25 JARs are used with older JDK
   - Solution: The profiles handle this automatically

3. **Dependency conflicts**
   - Modern dependency versions may conflict with very old libraries
   - Solution: Update your dependencies to compatible versions

## Future Considerations

When JDK 25 is officially released:
1. Update CI/CD pipelines to test both JDK 17 and JDK 25
2. Consider making JDK 25 the default after a transition period
3. Update documentation and examples for new Java features
4. Evaluate new JavaFX 25 features for integration

For questions or issues with the upgrade, please file an issue in the project repository.