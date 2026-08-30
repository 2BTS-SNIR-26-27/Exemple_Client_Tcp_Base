plugins {
    application
}

group = "fr.btsciel"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("fr.btsciel.Client_TCP_Base")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

val appName = "ClientTCP"
val mainClassName = "fr.btsciel.Client_TCP_Base"

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClassName
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Exec>("jpackageImage") {
    group = "distribution"
    description = "Crée une application portable console avec jpackage"

    dependsOn(tasks.jar)

    doFirst {
        val outputDir = layout.buildDirectory.dir("jpackage-image").get().asFile
        outputDir.deleteRecursively()
    }

    commandLine(
        "jpackage",
        "--type", "app-image",
        "--name", appName,
        "--input", tasks.jar.get().archiveFile.get().asFile.parentFile.absolutePath,
        "--main-jar", tasks.jar.get().archiveFileName.get(),
        "--main-class", mainClassName,
        "--dest", layout.buildDirectory.dir("jpackage-image").get().asFile.absolutePath,
        "--win-console"
    )
}

tasks.register<Exec>("jpackage") {
    group = "distribution"
    description = "Crée un installateur Windows .exe console avec jpackage"

    dependsOn(tasks.jar)

    doFirst {
        val outputDir = layout.buildDirectory.dir("jpackage").get().asFile
        outputDir.deleteRecursively()
    }

    commandLine(
        "jpackage",
        "--type", "exe",
        "--name", appName,
        "--app-version", project.version.toString(),
        "--vendor", "fr.btsciel",
        "--description", "Client TCP de base",
        "--input", tasks.jar.get().archiveFile.get().asFile.parentFile.absolutePath,
        "--main-jar", tasks.jar.get().archiveFileName.get(),
        "--main-class", mainClassName,
        "--dest", layout.buildDirectory.dir("jpackage").get().asFile.absolutePath,
        "--win-console",
        "--win-dir-chooser",
        "--win-menu",
        "--win-shortcut"
    )
}