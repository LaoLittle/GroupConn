plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.9.0-M1"
}

group = "org.laolittle.plugin.groupconn"
version = "1.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}