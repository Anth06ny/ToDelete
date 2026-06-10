import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    id("com.github.gmazzo.buildconfig") version "5.5.1"
}

// Read API key from local.properties
val localProperties = Properties() //import java.utils
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation("io.insert-koin:koin-android:4.1.+")

}

android {
    namespace = "com.amonteiro.todelete"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    //Récupérer les informations du keystore dans local.properties pour signer l'exécutable de sortie
    //A mettre avant buildTypes
    signingConfigs {
        // On ne crée la config de signature QUE si le keystore est défini en local.
        // En CI, local.properties n'a pas KEYSTORE_FILE -> on ne crée rien, donc pas
        // d'appel file("") qui planterait la configuration du projet.
        val keystoreFile = localProperties.getProperty("KEYSTORE_FILE")
        if (!keystoreFile.isNullOrBlank()) {
            create("release") {
                // rootProject.file -> chemin résolu depuis la racine (où est le keystore)
                storeFile = rootProject.file(keystoreFile)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS")
                keyPassword = localProperties.getProperty("KEY_PASSWORD")
            }
        }
    }

    defaultConfig {
        applicationId = "com.amonteiro.todelete"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            // findByName renvoie null si la config n'existe pas (CI sans keystore) :
            // le build release sera alors non signé, mais la configuration ne casse pas.
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}