rootProject.name = "fabric_annotations"
include("processor")
include("annotations")
include("example")

pluginManagement {
    repositories {
        jcenter()
        maven(url = "https://maven.fabricmc.net/")
        gradlePluginPortal()
    }

    val loomVersion: String by settings
    val kotlinVersion: String by settings
    plugins {
        id("fabric-loom") version loomVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("kapt") version kotlinVersion apply false
    }
}
