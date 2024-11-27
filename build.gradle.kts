buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.github.com/Giphy/giphy-android-sdk") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
        classpath("com.google.gms:google-services:4.4.0")
    }
}

plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.github.com/Giphy/giphy-android-sdk") }
    }
}