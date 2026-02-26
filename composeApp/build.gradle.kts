import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.kotlin.dsl.implementation
import java.util.Properties

// Leer local.properties para inyectar las claves de Supabase
val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Suprimir warning de beta para expect/actual classes
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        //iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.alpara.beus")
        }
    }
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // Configuración para cuando no hay variables de Xcode
            val platformName = System.getenv("PLATFORM_NAME") ?: "iphonesimulator"
            val archs = System.getenv("ARCHS") ?: "x86_64" // o "arm64" para M1+

            freeCompilerArgs += listOf(
                "-Xbinary=bundleId=com.alpara.beus"
            )
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.security.crypto)
        }
        commonMain.dependencies {
            implementation("dev.gitlive:firebase-auth:1.12.0")
            implementation("dev.gitlive:firebase-firestore:1.12.0")
            implementation("dev.gitlive:firebase-common:1.12.0")
            // SPACE
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.1")
            // SPACE
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            // Carga de imágenes KMP
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor)
            // Haze (blur)
            implementation(libs.haze)
            implementation(libs.haze.materials)
            // Iconos Material extended
            implementation(compose.materialIconsExtended)
            // Asegurar acceso a material-icons en KMP
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            // Fechas multiplataforma
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
            // SPACE
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.alpara.beus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.alpara.beus"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "0.2"

        buildConfigField("String", "SUPABASE_URL", "\"${localProperties["SUPABASE_URL"] ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties["SUPABASE_ANON_KEY"] ?: ""}\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.alpara.beus.resources"
}

dependencies {
    debugImplementation(compose.uiTooling)
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
}

