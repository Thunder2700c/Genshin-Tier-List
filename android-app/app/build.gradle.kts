plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val generatedWebsiteAssets = layout.buildDirectory.dir("generated/websiteAssets")

android {
    namespace = "com.thunder2700c.genshindpsarchive"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thunder2700c.genshindpsarchive"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
    }

    sourceSets.getByName("main").assets.srcDir(generatedWebsiteAssets)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")
}

val importWebsiteData by tasks.registering(Exec::class) {
    val sourceRoot = rootProject.projectDir.parentFile
    val script = rootProject.file("tools/import_website_data.py")
    val outputDirectory = generatedWebsiteAssets.get().asFile

    inputs.dir(sourceRoot.resolve("tiers"))
    inputs.dir(sourceRoot.resolve("assets/images/characters"))
    inputs.file(script)
    outputs.dir(outputDirectory)

    commandLine(
        "python3",
        script.absolutePath,
        "--source-root",
        sourceRoot.absolutePath,
        "--output-dir",
        outputDirectory.absolutePath
    )
}

tasks.named("preBuild").configure {
    dependsOn(importWebsiteData)
}
