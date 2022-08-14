import io.pzstorm.capsid.zomboid.ZomboidTasks

plugins {
    java
    id("io.pzstorm.capsid") version "0.4.2"
}

group = "io.drandarov"
version = "1.0.0"

repositories {
    mavenCentral()
}

tasks.register<GradleBuild>("setupWorkspace") {
    group = "zomboid"
    tasks = listOf("zomboidJar", "decompileZomboid", "annotateZomboid", "compileZomboid", "zomboidLuaJar")
}
