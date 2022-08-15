# Project Zomboid Decompiler Template

This project is a template for decompiling Project Zomboid source files and using them in a mod. Knowledge of Gradle is expected.

With this setup this project gathers the decompiled Project Zomboid source files and the mods themselves are seperate. __Step 1__ and __2__ are in this repository and __Step 3__ is in a seperate repository.

## Guide

### 1. Set `gameDir`

Set `gameDir` in the `local.properties` file to the location of your game files.

Example:

```properties
gameDir=D:/Games/SteamLibrary/steamapps/common/ProjectZomboid
```

### 2. Run gradle task `setupWorkspace`

Run `gradlew setupWorkspace` to decompile the game.

This creates a `lib` folder with the Project Zomboid code. This will be used in step 3.

### 3. Setup your mod project with Gradle

Create a `mod.info` file in the root of your mod project.

```properties
name=Example Mod.info
id=example_mod_info
modversion=1.0.0
poster=example_mod_info.png
description=Example Mod Info
url=https://github.com/drandarov-io/zomboid-decompiler-template
```

Create a `build.gradle.kts` with the following content:

```kotlin
plugins {
    java
}

val zomboidjar: String by project
val zomboidjarsources: String by project
val zomboidlua: String by project
val zomboidmedia: String by project

dependencies {
    implementation(files(zomboidjar))
    implementation(files(zomboidjarsources))
    implementation(files(zomboidlua))
    implementation(files(zomboidmedia))
}

sourceSets.create("media") {
    java.srcDir("media")

    compileClasspath += sourceSets.main.get().compileClasspath
}

val buildWorkshop by tasks.registering {
    val buildPath = "$buildDir/workshop/${project.name}"
    val modPath = "$buildPath/Contents/mods/${project.name}"

    group = "build"
    outputs.dir("$buildDir/workshop")

    doLast {
        copy {
            from("workshop/preview.png", "workshop/workshop.txt")
            into(buildPath)
        }

        copy {
            from("workshop/poster.png", "workshop/mod.info")
            into(modPath)
        }
        copy {
            from("media")
            into("$modPath/media")
        }
    }
}

val localDeploy by tasks.registering {
    val localPath = "${System.getProperties()["user.home"]}/Zomboid/Workshop"

    group = "build"
    outputs.dir("$localPath/${project.name}")

    dependsOn(buildWorkshop)

    doLast {
        copy {
            from(buildWorkshop.get().outputs.files)
            into(localPath)
        }
    }
}


val localUndeploy by tasks.registering {
    val localPath = "${System.getProperties()["user.home"]}/Zomboid/Workshop"

    group = "build"

    doLast {
        delete(localDeploy.get().outputs.files)
    }
}

```

Create the gradle.properties file with the following content:

```properties
zomboidjar = ../zomboid-decompiler-template/lib/zomboid.jar
zomboidjarsources = ../zomboid-decompiler-template/lib/zomboid-sources.jar
zomboidlua = ../zomboid-decompiler-template/lib/zdoc-lua-41.73.jar
zomboidmedia = D:/Games/SteamLibrary/steamapps/common/ProjectZomboid/media
```

The paths need to point to the jars generated in the second step.

Now you have a mod project ready to use with IntelliSense support:

![Screenshot of code with working documentation](./docs/result.png)

### 4. Notes

- `preview.png` must be 256x256 pixels
- After using the `localDeploy` task, you can upload your mod in-game, in the main menu when clicking `Workshop`.