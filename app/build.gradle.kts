plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fitup"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fitup"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    implementation("com.google.android.material:material:1.12.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.kevalpatel2106:ruler-picker:1.2")
}