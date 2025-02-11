plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mainfunctext"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mainfunctext"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "26.1.10909125"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(project(path = ":sdk"))
    implementation(libs.firebase.database)
    //implementation(libs.mediation.test.suite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(kotlin("script-runtime"))
    implementation (libs.ucrop)
    implementation(libs.justifiedtextview)
    implementation (libs.photoview)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.material.v190)
    implementation (libs.viewpager2)
    implementation (libs.core.ktx)
    implementation (libs.navigation.fragment)
    implementation (libs.navigation.ui)
    implementation (libs.lifecycle.livedata.ktx)
    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.camera.view)
    implementation (libs.camera.extensions)
    implementation (libs.recyclerview)
    implementation(libs.kotlin.script.runtime)

}