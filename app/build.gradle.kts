plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.prashant.smd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prashant.smd"
        minSdk = 21
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

    buildFeatures {

        viewBinding = true

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.android.gms:play-services-cast-framework:21.3.0")
    implementation("com.google.android.gms:play-services-fido:20.1.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.firebaseui:firebase-ui-database:7.1.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.nguyenhoanglam:ImagePicker:1.6.1")

    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.25")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")


}