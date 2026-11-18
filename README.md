# Integrated Propellers

Add some different function propeller for create aeronautics.

A NeoForge 1.21.1 addon for **Create: Aeronautics** that adds integrated propeller
bearings, eight separate propeller blade levels, and optional gyroscopic steering.

- **Author:** Alanna233
- **Minecraft:** 1.21.1
- **NeoForge:** 21.1.233
- **Create:** 6.0.10
- **Create: Aeronautics:** 1.3.2
- **Sable:** 2.0.6

## Features

### Propeller blades

Eight items, `propeller_blade_1` through `propeller_blade_8`, with increasing
length and stress impact.

- Level 1 is crafted from wooden slabs and andesite alloy.
- Levels 2–8 are crafted shapeless from two blades of the previous level.
- Blades are installed directly into either bearing; no separate contraption is
  assembled.

### Integrated Ordinary Bearing

- 1×1×1 kinetic block.
- Holds up to 6 blades.
- Top slot selects thrust direction relative to rotation.
- Create wrench can rotate its facing.

### Integrated Gyroscopic Bearing

A two-block structure:

- `integrated_gyroscopic_propeller_base` — lower/kinetic block, owns blades,
  frequencies, modes and stress.
- `integrated_gyroscopic_propeller_bearing` — upper propeller block and its
  top-thrust value box.

Features:

- Holds up to 12 blades.
- Four gyroscope modes:
  - **Normal** — follows local gravity/up with a clamped tilt cone.
  - **Stable** — normal alignment plus lateral velocity compensation.
  - **Gyroscope** — wireless redstone steering while anti-sway stays active.
  - **Manual Gyroscope** — gravity alignment plus wireless steering without
    velocity compensation.
- Two Create-compatible frequency slots per base face. Frequencies can also be
  bound directly with Create Linked Controller or Simulated typewriter keys.
- Anti-sway damping and lateral speed compensation are configurable.

## Ponder

The addon includes nine built-in Ponder scenes converted from the project's
Ponderer scripts:

- 4 scenes for the integrated gyroscopic bearing.
- 5 scenes for the integrated ordinary bearing.

All titles and step text are stored in:

```text
assets/aero_propeller_addon/lang/en_us.json
assets/aero_propeller_addon/lang/zh_cn.json
```

Ponderer is **not** required at runtime.

## Configuration

Server config file:

```text
config/aero_propeller_addon-server.toml
```

Useful options:

```toml
[gyroscopic]
antiSwayDamping = 2.0
velocityCompensationGain = 0.75
```

Both are clamped to safe ranges in code.

## Build

### Requirements

- JDK 21
- Gradle 8.x
- The dependency jars listed in `libs/README.md`

### Steps

1. Install JDK 21 and set `JAVA_HOME`.
2. Put the dependency jars listed in `libs/README.md` into `libs/`.
3. Run one of:

```bat
build.bat
```

```bash
./gradlew build
```

`build.bat` checks the dependency jars and then invokes the Gradle wrapper.
The output jar is written to `build/libs/`.

The project uses NeoForge ModDevGradle and compiles against the supplied
dependencies as `compileOnly`; dependency jars are not bundled into the addon.

## Project layout

```text
src/main/java/com/aeropropeller/addon/       Addon code
src/main/resources/assets/                  Models, textures, language, Ponder
src/main/resources/data/                    Recipes, tags and loot
gradle/wrapper/                             Gradle wrapper
gradlew / gradlew.bat                       Wrapper launchers
build.bat / build.sh                        Local one-step build scripts
libs/                                       Local compile-time dependencies
```

## Credits

- **Alanna233** — author.
- Create, Create: Aeronautics, Simulated, Sable and Ponder are the work of
  their respective authors.

## License

MIT — see [LICENSE](LICENSE).
