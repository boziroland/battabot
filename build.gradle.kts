import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.9.0"

	id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.battabot"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("com.google.cloud:google-cloud-translate:2.57.0")
    implementation("dev.arbjerg:lavaplayer:2.2.2")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("org.json:json:20231013")
    implementation("org.apache.commons:commons-text:1.13.1")
    implementation("commons-io:commons-io:2.19.0")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("org.jsoup:jsoup:1.20.1")

    testImplementation(kotlin("test"))
}

repositories {
    maven {
        url = uri("https://m2.dv8tion.net/releases")
        name = "m2-dv8tion"
    }
    maven {
        url = uri("https://dl.bintray.com/sedmelluq/com.sedmelluq")
        maven { url = uri("https://jitpack.io") }
    }
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("AppKt")
}
