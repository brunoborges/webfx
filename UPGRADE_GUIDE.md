# WebFX Java 25 Upgrade Guide

This guide documents the upgrade of WebFX to Java 25 and JavaFX 25.

## Overview

The WebFX project has been upgraded to Java 25 and JavaFX 25 as the default build target, while maintaining backward compatibility with Java 17.

## Changes Made

### Maven Configuration Updates

1. **Parent POM (`pom.xml`)**:
   - Updated OpenJFX parent version from `21` to `25`
   - Changed default Maven compiler release from `17` to `25`
   - Updated default JavaFX version to `25`
   - Renamed profile `jdk25` to `jdk17-compat` for backward compatibility
   - Maven compiler plugin remains at `3.13.0` for Java 25 support
   - ASM dependency at `9.7` for better JDK 25 compatibility

2. **Module Updates**:
   - **webfx-component**: Compatible with JavaFX 25
   - **webfx-browser**: Compatible with JavaFX 25 
   - **webfx-deck**: Compatible with JavaFX 25

3. **GitHub Actions**:
   - Added comprehensive CI/CD workflow
   - Tests with Java 25 (default)
   - Tests with Java 17 (compatibility)
   - Multi-platform verification (Ubuntu, Windows, macOS)

### Dependency Updates

| Component | Old Version | New Version |
|-----------|-------------|-------------|
| OpenJFX | 21 | 25 |
| Default JDK Target | 17 | 25 |
| Maven Compiler Plugin | 3.13.0 | 3.13.0 |
| ASM | 9.7 | 9.7 |
| Groovy JSR223 | 3.0.21 | 3.0.21 |
| Apache HttpClient | 4.5.14 | 4.5.14 |
| Jersey Client | 3.1.8 | 3.1.8 |
| Tyrus WebSocket | 2.1.5 | 2.1.5 |
| JUnit | 4.13.2 | 4.13.2 |
| Maven Surefire Plugin | 3.2.5 | 3.2.5 |
| Maven Dependency Plugin | 3.6.1 | 3.6.1 |
| Exec Maven Plugin | 3.4.1 | 3.4.1 |

## Build Profiles

The project now uses Java 25 as the default target with backward compatibility support:

### Default Profile (JDK 25)
```bash
mvn clean compile
```
Uses JavaFX 25 and targets Java 25.

### JDK 17 Compatibility Profile
```bash
mvn clean compile -Dtarget.jdk=17
```
For users who need to stay on Java 17, this profile uses JavaFX 21 and targets Java 17.

## Migration Path

### For New Users
- Install JDK 25 (available from Adoptium/Temurin)
- Build normally with `mvn clean install`
- All functionality works with Java 25

### For Existing Users (JDK 17-24)
You have two options:

**Option 1: Upgrade to JDK 25 (Recommended)**
1. Install JDK 25
2. Build with `mvn clean install`
3. Enjoy the latest Java features and performance improvements

**Option 2: Stay on JDK 17**
1. Keep using JDK 17
2. Use the compatibility profile: `mvn clean install -Dtarget.jdk=17`
3. All functionality preserved with JavaFX 21

## Benefits of the Upgrade

1. **Latest Java Features**: Access to all Java 25 language features and APIs
2. **Modern JavaFX**: JavaFX 25 with latest improvements and bug fixes
3. **Improved Security**: Latest security updates and patches
4. **Better Performance**: Modern JVM optimizations
5. **Backward Compatible**: Still works with JDK 17 via profile
6. **CI/CD Pipeline**: Automated testing with GitHub Actions

## Compatibility

- **Default JDK**: 25
- **Default JavaFX**: 25
- **Backward Compatible**: JDK 17 with JavaFX 21 (via `-Dtarget.jdk=17`)
- **Operating Systems**: Windows, macOS, Linux (all supported by JavaFX)

## Building and Running

### Prerequisites
- JDK 25 (or JDK 17 for compatibility mode)
- Maven 3.6+

### Build Commands
```bash
# Default build (JDK 25, JavaFX 25)
mvn clean install

# JDK 17 compatibility build
mvn clean install -Dtarget.jdk=17

# Run WebFX Browser
mvn exec:java -pl webfx-browser

# Run WebFX Deck
mvn exec:java -pl webfx-deck
```

## CI/CD with GitHub Actions

The project now includes a comprehensive CI/CD pipeline that:
- Builds and tests with Java 25 (default)
- Builds and tests with Java 17 (compatibility)
- Verifies builds on Linux, Windows, and macOS
- Uploads build artifacts for both Java versions

Workflows are triggered on:
- Push to main/master branch
- Pull requests
- Manual workflow dispatch

## Testing

The upgrade maintains full compatibility with existing functionality:
- All JavaFX controls and features work as before
- FXML loading and processing unchanged
- WebView functionality preserved
- Security model maintained

## Troubleshooting

### Common Issues

1. **"Release version 25 not supported"**
   - This occurs when using JDK < 25 without the compatibility profile
   - Solution: Either upgrade to JDK 25, or use `-Dtarget.jdk=17` to build with JDK 17

2. **"Class file has wrong version"**
   - This occurs when JavaFX 25 JARs are used with older JDK
   - Solution: Use the correct profile: `-Dtarget.jdk=17` for JDK 17

3. **Dependency conflicts**
   - Modern dependency versions may conflict with very old libraries
   - Solution: Update your dependencies to compatible versions

4. **GitHub Actions workflow not running**
   - Ensure your branch is set to main or master
   - Check that workflows are enabled in repository settings

## Future Considerations

As Java evolves:
1. CI/CD pipelines now test both JDK 17 and JDK 25 automatically
2. Java 25 is now the primary development target
3. New Java features can be adopted immediately
4. JavaFX 25 features are fully available

For questions or issues with the upgrade, please file an issue in the project repository.