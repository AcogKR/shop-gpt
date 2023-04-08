import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc
import kr.entree.spigradle.kotlin.vault

plugins {
    kotlin("jvm") version "1.8.0"
    id("kr.entree.spigradle") version "2.4.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.acog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    papermc()
    vault()
}

dependencies {
    compileOnly(paper("1.19.3"))
    compileOnly(vault())
    implementation("io.typecraft:bukkit-view-core:5.1.2")
    implementation("io.heartpattern.springfox:springfox-starter:0.1.15")
    implementation(fileTree("libs") { include("*.jar") })
}

tasks {
    assemble.get().dependsOn(shadowJar)
}

spigot {
    depends = listOf("Vault")
    apiVersion = "1.19"

    commands {
        create("shop")
    }
}