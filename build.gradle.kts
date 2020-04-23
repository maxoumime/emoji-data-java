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
    implementation(kotlin("test-junit"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.maximebertheau"
            artifactId = "emoji-data-java"
            version = "2.0"

            from(components["java"])
        }
    }
}