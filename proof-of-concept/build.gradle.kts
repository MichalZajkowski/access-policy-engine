plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.13.0")
    implementation("com.deque.html.axe-core:selenium:4.8.0")
    implementation("org.yaml:snakeyaml:2.2")
}

application {
    mainClass.set("MainKt")
}

tasks.withType<JavaCompile>().configureEach {
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
