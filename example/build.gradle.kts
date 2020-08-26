plugins {
    java
    idea
    id("fabric-loom")
    `maven-publish`
    kotlin("jvm")
    kotlin("kapt")
}

val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

version = modVersion
group = mavenGroup

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceSets["main"].java {
        srcDir("${buildDir.absolutePath}/generated/source/kaptKotlin/")
    }
}

base {
    archivesBaseName = archivesBaseName
}

kapt {
    // Required line!
    annotationProcessor("com.thinkslynk.fabric.annotations.FabricProcessor")
}

minecraft {
}

repositories {
    maven(url = "http://maven.fabricmc.net/")
}

dependencies {
    val minecraftVersion: String by project
    val yarnMappings: String by project
    val loaderVersion: String by project
    val fabricVersion: String by project
    val fabricKotlinVersion: String by project

    //to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":annotations"))
    kapt(project(":processor"))
}

val processResources = tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        filter { line -> line.replace("%VERSION%", "${project.version}") }
    }
}


val javaCompile = tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}


// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}


//// configure the maven publication
//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            // add all the jars that should be included when publishing to maven
//            artifact(remapJar) {
//                builtBy remapJar
//            }
//            artifact(sourcesJar) {
//                builtBy remapSourcesJar
//            }
//        }
//    }
//
//    // select the repositories you want to publish to
//    repositories {
//        // uncomment to publish to the local maven
//        // mavenLocal()
//    }
//}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}