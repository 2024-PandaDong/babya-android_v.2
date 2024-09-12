import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.protobuf")
    id("kotlin-kapt")
}

android {
    namespace = "kr.pandadong2024.babya"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.pandadong2024.babya"
        minSdk = 27
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.1"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    // chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.tbuonomo:dotsindicator:5.0")

    implementation(libs.google.material.v190)

    implementation(libs.dotsindicator)

    implementation(libs.circleindicator)

    implementation(libs.androidx.room.runtime.v250)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.material3.android)
    kapt(libs.androidx.room.compiler)

    implementation(libs.logging.interceptor)

    implementation(libs.coil)
    implementation(libs.coil.svg)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.circleimageview)

    implementation(libs.okhttp)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.activity.v190)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //datastore
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)

    implementation(libs.converter.gson)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v1110)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}