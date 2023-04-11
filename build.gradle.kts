plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"

    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.github.aquabtww"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.triumphteam.dev/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.8.0")

    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0")

    compileOnly("net.luckperms:api:5.4")

    compileOnly("io.lumine:Mythic-Dist:5.2.1")

    implementation("dev.triumphteam:triumph-gui:3.1.4")
}

bukkit {
    name = "Chambers"
    apiVersion = "1.19"
    authors = listOf("aquabtw")
    main = "io.github.aquabtww.Chambers"
    depend = listOf("LuckPerms", "MythicMobs")

    commands {
        register("adminmanage") {
            aliases = listOf("amng")
        }
        register("chambers")
        register("party")
        register("stash")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}