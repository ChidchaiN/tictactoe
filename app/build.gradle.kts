plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.tictactoe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tictactoe"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidxCore) // core-ktx
    implementation(libs.appcompat) // appcompat
    implementation(libs.material) // material
    implementation(libs.activity) // activity-ktx
    implementation(libs.constraintlayout) // constraintlayout
    implementation(libs.coroutinesCore) // kotlinx-coroutines-core
    implementation(libs.navigation) // navigation-fragment-ktx
    implementation(libs.roomRuntime) // room-runtime
    implementation(libs.lifecycleLivedata) // lifecycle-livedata-ktx
    implementation(libs.retrofit) // retrofit2
    implementation(libs.converterGson) // retrofit2:converter-gson
    implementation(libs.okhttp) // okhttp
    implementation(libs.logging) // okhttp3:logging-interceptor
    implementation(libs.gson) // gson

    testImplementation(libs.junit) // junit
    androidTestImplementation(libs.espressoCore) // espresso-core
}
