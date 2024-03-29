plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
}

android {
    namespace = "com.example.ffff"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ffff"
        minSdk = 24
        targetSdk = 33
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("com.cashfree.pg:api:2.1.4")

    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.23.0@aar")
    implementation ("com.amazonaws:aws-android-sdk-s3:2.23.0")
    implementation ("com.amazonaws:aws-android-sdk-cognito:2.6.1")

     implementation ("com.twilio:chat-android:5.1.1")
     implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0")
     implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
     implementation ("com.twilio:voice-android:5.3.1")
     implementation ("com.twilio:audioswitch:0.1.3")
     implementation ("com.twilio:voice-android:5.3.1")
     implementation ("androidx.lifecycle:lifecycle-process:2.4.1")
     implementation ("com.twilio:video-android:5.5.0")

}