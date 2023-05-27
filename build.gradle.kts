plugins {
    kotlin("jvm") version "1.8.0"
    java
    application
}

group = "com.pizza.stream"
version = "1.0-SNAPSHOT"

val logbackVersion: String by project
val jacksonVersion: String by project
val apacheCommonsVersion: String by project

repositories {
    mavenCentral()
}

//kotlin {
//    jvmToolchain(17)
//}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("PizzaStreamApp")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.apache.kafka:kafka-streams:3.4.0")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.apache.commons:commons-lang3:$apacheCommonsVersion")

    testImplementation(kotlin("test"))
}
