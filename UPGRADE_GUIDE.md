# WebFX Java 25 Upgrade Guide

This guide documents the upgrade of WebFX to Java 25 and JavaFX 25.

## Overview

WebFX now targets Java 25 and JavaFX 25.

## Changes Made

### Maven Configuration Updates

1. **Parent POM (`pom.xml`)**:
   - Updated OpenJFX parent version from `21` to `25`
   - Set Maven compiler release to `25`
   - Set JavaFX version to `25`
   - Maven compiler plugin remains at `3.13.0` for Java 25 support
   - ASM dependency remains at `9.7`

2. **Module Updates**:
   - **webfx-component**: JavaFX 25
   - **webfx-browser**: JavaFX 25
   - **webfx-deck**: JavaFX 25

3. **GitHub Actions**:
   - Build and test with Java 25
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

## Building and Running

### Prerequisites
- JDK 25
- Maven 3.6+

### Build Commands
```bash
# Build (JDK 25, JavaFX 25)
mvn clean install

# Run WebFX Browser
mvn exec:java -pl webfx-browser

# Run WebFX Deck
mvn exec:java -pl webfx-deck
```

## CI/CD with GitHub Actions

The project CI pipeline:
- Builds and tests with Java 25
- Verifies builds on Linux, Windows, and macOS

Workflows are triggered on:
- Push to main/master branch
- Pull requests
- Manual workflow dispatch

## Troubleshooting

### Common Issues

1. **"Release version 25 not supported"**
   - Cause: Using JDK lower than 25
   - Solution: Upgrade to JDK 25

2. **Dependency conflicts**
   - Cause: Modern dependency versions may conflict with very old libraries
   - Solution: Update dependencies to compatible versions

3. **GitHub Actions workflow not running**
   - Ensure your branch is set to main or master
   - Check that workflows are enabled in repository settings

## Future Considerations

As Java evolves:
1. Java 25 is the primary development target
2. New Java features can be adopted immediately
3. JavaFX 25 features are fully available

For questions or issues with the upgrade, please file an issue in the project repository.
