plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.ucompensar.eldesvare"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ucompensar.eldesvare"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // 1. Navegación en Jetpack Compose y ViewModel (MVVM)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 2. Íconos extendidos de Material 3 (la versión la da el BOM de Compose)
    implementation(libs.androidx.compose.material.icons.extended)

    // 3. Google Maps SDK para Compose y Servicios de Ubicación (GPS)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // 4. Room (Base de datos local SQLite para soporte offline)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // 5. Coil (Para mostrar la foto tomada con cámara o elegida de galería)
    implementation(libs.coil.compose)

    // 6. Firebase: Cloud Firestore (el BoM fija las versiones)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
