plugins {
    application

    // JavaFX / FXML
        // id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "fr.btsciel"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    // Version console actuelle
    mainClass.set("Client_TCP_Base")

    // Si package dans ton fichier Java :
        // mainClass.set("fr.btsciel.Client_TCP_Base")

    // JavaFX / FXML, mets ici ta classe Application JavaFX :
        // mainClass.set("fr.btsciel.MainApplication")
}

/*
 * JavaFX / FXML
 * À décommenter uniquement si ton projet utilise JavaFX.
 */
/*
javafx {
    version = "25"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}
*/

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Dépendances JavaFX / FXML
    // Normalement inutiles si tu utilises le plugin org.openjfx.javafxplugin
    // implementation("org.openjfx:javafx-controls:25")
    // implementation("org.openjfx:javafx-fxml:25")
}

tasks.test {
    useJUnitPlatform()
}

val appName = "ClientTCP"
val mainClassName = "Client_TCP_Base"
val appIcon = layout.projectDirectory.file("src/main/packaging/ClientTCP.ico")

// Si  package dans ton fichier Java :
// val mainClassName = "fr.btsciel.Client_TCP_Base"

// JavaFX / FXML :
// val mainClassName = "fr.btsciel.MainApplication"

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClassName
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

/*
 * Image portable de l'application.
 * Résultat dans : build/jpackage-image
 */
tasks.register<Exec>("jpackageImage") {
    group = "distribution"
    description = "Crée une image exécutable portable avec jpackage"

    dependsOn("jar")

    val jarFile = tasks.jar.get().archiveFile.get().asFile
    val outputDir = layout.buildDirectory.dir("jpackage-image").get().asFile

    doFirst {
        check(appIcon.asFile.isFile) {
            "Icône absente : ${appIcon.asFile}. Ajoutez un fichier .ico à cet emplacement."
        }
        outputDir.deleteRecursively()
    }

    commandLine(
        "jpackage",
        "--type", "app-image",
        "--name", appName,
        "--input", jarFile.parentFile.absolutePath,
        "--main-jar", jarFile.name,
        "--main-class", mainClassName,
        "--icon", appIcon.asFile.absolutePath,
        "--dest", outputDir.absolutePath,
        "--win-console"
    )
}

/*
 * Installateur Windows .exe.
 *
 * Prérequis pour créer un .exe :
 * winget install WixToolset.WixToolset
 *
 * Résultat dans : build/jpackage
 */
tasks.register<Exec>("jpackage") {
    group = "distribution"
    description = "Crée un installateur Windows .exe avec jpackage"

    dependsOn("jar")

    val jarFile = tasks.jar.get().archiveFile.get().asFile
    val outputDir = layout.buildDirectory.dir("jpackage").get().asFile

    doFirst {
        check(appIcon.asFile.isFile) {
            "Icône absente : ${appIcon.asFile}. Ajoutez un fichier .ico à cet emplacement."
        }
        outputDir.deleteRecursively()
    }

    commandLine(
        "jpackage",
        "--type", "exe",
        "--name", appName,
        "--app-version", project.version.toString(),
        "--vendor", "fr.btsciel",
        "--description", "Client TCP de base",
        "--input", jarFile.parentFile.absolutePath,
        "--main-jar", jarFile.name,
        "--main-class", mainClassName,
        "--icon", appIcon.asFile.absolutePath,
        "--dest", outputDir.absolutePath,
        "--win-console",
        "--win-dir-chooser",
        "--win-menu",
        "--win-shortcut"
    )
}
