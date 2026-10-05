# Cosmic — Fabric 26.2

A client-side, cosmetic-only Minecraft mod for Fabric 26.2. Cosmic recreates flashy hit/world visuals without providing gameplay assistance.

## Menu

Open the in-game **COSMIC** menu with:

`/cosmic menu`

The menu includes:
- Master enable/disable
- Hit particles
- Critical burst
- Kill burst
- Hit flash
- Block/world FX
- Hit particle count
- Critical particle count
- Particle spread
- Particle speed
- Test Effects

All settings are saved to:

`.minecraft/config/cosmeticfx.properties`

## Commands

- `/cosmic menu`
- `/cosmic toggle`
- `/cosmic test`
- `/cosmeticfx toggle`
- `/cosmeticfx reload`
- `/cosmeticfx test`

## Safety / scope

Cosmic is intentionally visual-only and client-side. It does not modify reach, aiming, movement, combat mechanics, packets, automation, entity information, or server state.

## Build

Requires Java 25 for Minecraft 26.2.

`./gradlew build`
