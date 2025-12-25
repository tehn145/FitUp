import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
}

val groqApiKey: String = try {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    properties.getProperty("GROQ_API_KEY") ?: ""
} catch (e: Exception) {
    ""
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
        buildConfigField("String", "GROQ_API_KEY", "\"$groqApiKey\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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

    implementation("com.mapbox.maps:android-ndk27:11.17.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.storage)
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-location:21.3.0")
//    implementation("com.google.android.material:material:1.12.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.kevalpatel2106:ruler-picker:1.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.firebaseui:firebase-ui-storage:8.0.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

}