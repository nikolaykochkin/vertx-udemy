import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.freefair.lombok") version "8.1.0"
}

group = "name.nikolaikochkin"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.4.4"
val logbackVersion = "1.4.8"
val nettyVersion = "4.1.94"
val mutinyVertxVersion = "3.5.0"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "name.nikolaikochkin.vertx_mutiny.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
}

dependencies {
    implementation(platform("io.smallrye.reactive:vertx-mutiny-clients-bom:$mutinyVertxVersion"))
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-core")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-web")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-pg-client")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.testcontainers:postgresql:1.18.3")
    implementation("org.postgresql:postgresql:42.6.0")

    testImplementation("io.smallrye.reactive:smallrye-mutiny-vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
