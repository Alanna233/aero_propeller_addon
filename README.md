# Integrated Propellers

Add some different function propeller for create aeronautics.

A NeoForge 1.21.1 addon for **Create: Aeronautics** that adds integrated propeller
bearings, eight separate propeller blade levels, and optional gyroscopic steering.

- **Minecraft:** 1.21.1
- **NeoForge:** 21.1.233
- **Create:** 6.0.10
- **Create: Aeronautics:** 1.3.2
- **Sable:** 2.0.6

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
