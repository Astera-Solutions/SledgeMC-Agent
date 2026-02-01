# Sledge Agent

The **Bytecode Manipulator** for SledgeMC.

**Sledge Agent** is a Java Agent that attaches to the JVM process to provide low-level instrumentation support.

## Role
- Attaches to the Minecraft process during startup.
- Provides a bridge for Mixin to transform classes before they are loaded by the JVM.

## Features
- **Early Instrumentation**: Ensures transformations are applied before class definition.
- **SpongePowered Mixin Support**: Bootstraps the Mixin environment in the premain stage.

## Build
To build the Agent jar:
```bash
./gradlew clean build
```
The artifact will be located in `build/libs/sledge-agent-1.0.0-SNAPSHOT.jar`.
