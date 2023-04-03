import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.7.21"

	id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "org.battabot"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:5.0.0-beta.6")
    implementation("com.google.cloud:google-cloud-translate:1.88.0")
    implementation("com.github.Walkyst:lavaplayer-fork:1.3.96")
    implementation("ch.qos.logback:logback-classic:1.2.9")

	implementation("com.github.Doomsdayrs:Jikan4java:v1.4.2")
    implementation("org.json:json:20220320")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("net.lingala.zip4j:zip4j:2.10.0")

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
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("Bot.App")
}
