import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenLocal()
    maven("https://repo.maven.apache.org/maven2")
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20140107")
    testImplementation("junit:junit:4.12")
    implementation(kotlin("stdlib-jdk8"))
}

group = "com.maximebertheau"
version = "1.1"
description = "emoji-java"

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xinline-classes")
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
