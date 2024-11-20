import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    checkstyle
    jacoco
    id("io.freefair.lombok") version "6.6.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
    //lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    //javalin и jte
    implementation("gg.jte:jte:3.1.12")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.javalin:javalin:6.2.0")
    implementation("io.javalin:javalin-bundle:6.2.0")
    implementation("io.javalin:javalin-rendering:6.1.6")
    //БД
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    //Unirest для запросов
    implementation("com.mashape.unirest:unirest-java:1.4.9")
    implementation("org.jsoup:jsoup:1.7.2")

}

application {
    mainClass.set("hexlet.code.App")
}

tasks.test {
    useJUnitPlatform()
    // https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        // showStackTraces = true
        // showCauses = true
        showStandardStreams = true
    }
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }