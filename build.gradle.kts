import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenLocal()
    maven("https://repo.maven.apache.org/maven2")
    mavenCentral()
    jcenter()

}

dependencies {
    implementation("org.json:json:20140107")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("test-junit"))
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

            pom {
                packaging = "jar"
                url.set("https://github.com/maxoumime/emoji-data-java")
                description.set("The missing emoji library for Java, using emoji-data.")
                developers {
                    developer {
                        email.set("maxime.bertheau@gmail.com")
                        name.set("Maxime Bertheau")
                        url.set("https://www.maximebertheau.com")
                    }
                }

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/maxoumime/emoji-data-java")
                    connection.set("scm:git:git://github.com/maxoumime/emoji-data-java.git")
                    developerConnection.set("scm:git:ssh://github.com/maxoumime/emoji-data-java.git")
                }

                distributionManagement {
                    repositories {
                        maven {

                            credentials {
                                username = rootProject.property("BINTRAY_USERNAME") as String?
                                password = rootProject.property("BINTRAY_API_KEY") as String?
                            }

                            name = "maxoumime-emoji-data-java"
                            url = "https://api.bintray.com/maven/maxoumime/emoji-data-java/emoji-data-java/;publish=1".let(::URI)
                        }
                    }
                }
            }
        }
    }
}
