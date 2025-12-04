plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    testImplementation(libs.junit)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.13.0")
// minimal 1.10.0

    implementation("androidx.preference:preference:1.2.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    androidTestImplementation(libs.ext.junit)
    implementation("androidx.cardview:cardview:1.0.0")
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
// Use the latest stable version
}