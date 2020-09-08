plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
}

group = "org.thinkslynk.fabric_annotations"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(project(":annotations"))

    implementation("com.squareup:kotlinpoet:1.6.0")
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions{
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.ExperimentalStdlibApi","-Xopt-in=kotlin.contracts.ExperimentalContracts")
        }
    }
    compileTestKotlin {
        kotlinOptions{
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.ExperimentalStdlibApi","-Xopt-in=kotlin.contracts.ExperimentalContracts")
        }
    }
}