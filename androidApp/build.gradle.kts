import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.koga.deeplinklauncher.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "dev.koga.deeplinklauncher.android"
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}

tasks.withType(KotlinCompile::class.java) {
    dependsOn("exportLibraryDefinitions")
}