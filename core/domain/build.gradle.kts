import extension.setFrameworkBaseName

plugins {
    id("dev.koga.deeplinklauncher.multiplatform")
    kotlin("plugin.serialization") version "1.9.20"
}

kotlin {
    setFrameworkBaseName("domain")
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.immutable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
    }
}

android {
    namespace = "dev.koga.domain"
}
