import org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val name: String by project
val ideaVersion: String by project
val kotlinVersion: String by project
val javaVersion: String by project
val kotlinLanguageVersion: String by project
val kotlinTargetVersion: String by project
val pluginVerifierIdeVersions: String by project
val publishChannels: String by project

plugins {
    id("org.jetbrains.intellij") version "1.3.0"
    id("com.adarshr.test-logger") version "3.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("idea")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
    maven(url = "https://maven.aliyun.com/nexus/content/groups/public/")
    maven(url = "https://repo.eclipse.org/content/groups/releases/")
    maven(url = "https://www.jetbrains.com/intellij-repository/releases")
    maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
}

intellij {
    pluginName.set(name)

    version.set(ideaVersion)
    //localPath = '/Users/izhangzhihao/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/201.6668.121/IntelliJ IDEA 2020.1 EAP.app/Contents'
    //localPath = '/Users/izhangzhihao/Library/Application Support/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/CLion.app/Contents'
    updateSinceUntilBuild.set(false)

    plugins.set(
        listOf(
            "java",
            "java-i18n",
            "JavaScriptLanguage",
            "DatabaseTools",
            "CSS",
            "platform-images",
            "Groovy",
            "properties",
            "yaml",
            "org.jetbrains.kotlin:203-$kotlinVersion-release-IJ5981.133-1",
            "org.intellij.scala:2020.3.21",
            "Dart:203.5981.155",
            "org.jetbrains.plugins.ruby:203.5981.155",
            "com.jetbrains.php:203.5981.155",
            "com.jetbrains.sh:203.5981.37",
            "com.jetbrains.plugins.jade:203.5981.155",
            "org.jetbrains.plugins.go-template:203.5981.155",
            "Pythonid:203.5981.155",
        )
    )
}

tasks {
    runIde {
        systemProperties["idea.auto.reload.plugins"] = false
        jvmArgs = listOf(
            "-Xms512m",
            "-Xmx2048m",
        )
    }

    publishPlugin {
        token.set(System.getenv("token"))
        channels.set(publishChannels.split(",").map { it.trim() }.toList())
    }

    runPluginVerifier {
        ideVersions.set(pluginVerifierIdeVersions.split(",").map { it.trim() }.toList())
        failureLevel.set(listOf(COMPATIBILITY_PROBLEMS))
    }

    testlogger {
        theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
    }
}

dependencies {
    //implementation("org.eclipse.mylyn.github:org.eclipse.egit.github.core:5.11.0.202103091610-r") {
    //    exclude("gson")
    //}
    compileOnly(fileTree("libs"))
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.languageVersion = kotlinLanguageVersion
    kotlinOptions.apiVersion = kotlinTargetVersion
    kotlinOptions.jvmTarget = javaVersion
    kotlinOptions.freeCompilerArgs = listOf("-Xskip-runtime-version-check", "-Xjsr305=strict")
}
