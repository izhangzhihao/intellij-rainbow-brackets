import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val name: String by project
val ideaVersion: String by project
val javaVersion: String by project
val kotlinLanguageVersion: String by project
val kotlinTargetVersion: String by project
val publishChannels: String by project

plugins {
    id("org.jetbrains.intellij.platform") version "2.11.0"
    id("com.adarshr.test-logger") version "3.2.0"
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
    id("idea")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
    maven(url = "https://maven.aliyun.com/nexus/content/groups/public/")
    maven(url = "https://www.jetbrains.com/intellij-repository/releases")
    maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
    intellijPlatform {
        defaultRepositories()
    }
}

tasks {
    testlogger {
        theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
    }

    jar {
        archiveBaseName.set("intellij-rainbow-brackets-lite")
    }
}

dependencies {
    compileOnly(fileTree("libs"))
    testImplementation("io.kotest:kotest-assertions-core:5.5.0")
    intellijPlatform {
        intellijIdea(ideaVersion)
        bundledPlugins(
            "com.intellij.java",
            "JavaScript",
            "org.intellij.groovy",
            "org.jetbrains.kotlin",
        )
        compatiblePlugins(
            "Dart",
            "com.jetbrains.php",
            "com.jetbrains.sh",
            "com.jetbrains.plugins.jade",
            "org.jetbrains.plugins.go-template",
        )
        testFramework(TestFrameworkType.Platform)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

intellijPlatform {
    pluginConfiguration {
        name = "intellij-rainbow-brackets-lite"
        ideaVersion {
            sinceBuild = "251"
            untilBuild = provider { null }
        }
    }
    publishing {
        token = providers.environmentVariable("token")
        channels = publishChannels.split(",").map { it.trim() }.toList()
    }
}

tasks.named<JavaExec>("runIde") {
    systemProperty("idea.auto.reload.plugins", "false")
    // Disable AI / full-line related bundled plugins in sandbox to avoid llama context startup and OOM.
    systemProperty(
        "idea.suppressed.plugins.id",
        listOf(
            "com.intellij.completion.full.line",
            "org.jetbrains.completion.full.line",
            "com.intellij.full.line",
            "com.intellij.ml.llm",
            "com.intellij.ml.llm.core",
            "com.jetbrains.ai",
            "com.jetbrains.codeWithMe",
        ).joinToString(",")
    )
    jvmArgs(
        "-Xms1024m",
        "-Xmx4096m",
        "-XX:MaxMetaspaceSize=1024m",
        "-XX:+UseG1GC",
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.languageVersion = kotlinLanguageVersion
    kotlinOptions.apiVersion = kotlinTargetVersion
    kotlinOptions.jvmTarget = javaVersion
    kotlinOptions.freeCompilerArgs = listOf("-Xskip-metadata-version-check", "-Xjsr305=strict")
}
