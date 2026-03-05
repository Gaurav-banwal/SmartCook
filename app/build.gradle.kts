plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")

}
//kjbkhj
//
android {
    namespace = "com.gaurav.smartcook"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        //
        applicationId = "com.gaurav.smartcook"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
//

dependencies {

    val room_version = "2.7.0-alpha01"

    implementation("androidx.room:room-runtime:$room_version")

    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation("com.google.dagger:hilt-android:2.51.1")

    kapt("com.google.dagger:hilt-compiler:2.51.1")
    // Firebase BOM (manages versions)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")



    // Retrofit & Moshi (JSON Parsing)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")


    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}