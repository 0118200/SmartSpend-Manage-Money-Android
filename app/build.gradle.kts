plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.jetbrains.kotlin.android) // Hapus jika kamu tidak menggunakan Kotlin
    // alias(libs.plugins.kotlin.kapt) // Hapus jika kamu tidak menggunakan Kotlin atau Room
}


android {
    // 🔥 Ganti namespace dan applicationId kembali ke com.example.smartspend
    namespace = "com.example.smartspend"
    compileSdk = 34 // Gunakan versi stabil

    defaultConfig {
        // 🔥 Ganti applicationId kembali ke com.example.smartspend
        applicationId = "com.example.smartspend"
        minSdk = 24 // Disesuaikan dengan fitur Notification Listener
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Gunakan Java 8 untuk kompatibilitas
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // Hapus blok kotlinOptions jika kamu tidak menggunakan Kotlin
    // kotlinOptions {
    //     jvmTarget = "1.8"
    // }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)


    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)

    // 🔥 Tambahkan ini untuk RecyclerView
    implementation(libs.androidx.recyclerview)
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Untuk ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Untuk Material Design Components (CardView, FloatingActionButton, BottomNavigationView)
    implementation("com.google.android.material:material:1.10.0")

// Untuk RecyclerView (jika kamu pakai)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}