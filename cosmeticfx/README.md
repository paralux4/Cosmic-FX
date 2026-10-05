# Cosmic — Fabric 26.2

Cosmic is a client-side cosmetic visual-effects mod for Minecraft 26.2.

## Build

Requirements:
- Java 25
- Internet access for the first Gradle bootstrap/dependency download

### Linux/macOS

```bash
chmod +x ./gradlew
./gradlew build
```

### Windows

```bat
gradlew.bat build
```

The launcher in this project bootstraps Gradle 9.5.1 from the URL in `gradle/wrapper/gradle-wrapper.properties`, so the repository does not depend on a globally installed Gradle executable.

## GitHub Actions

`.github/workflows/main.yml` builds the mod on pushes, pull requests, and manual runs. It uploads the resulting JAR as an artifact. Pushing a tag such as `v0.1.0` also creates a GitHub Release and attaches the JAR.

## In-game

Use `/cosmic menu` to open the Cosmic settings menu.

The mod is intended to remain visual/client-side only. It does not implement reach changes, aim assistance, automation, packet manipulation, movement changes, or server-side gameplay changes.
