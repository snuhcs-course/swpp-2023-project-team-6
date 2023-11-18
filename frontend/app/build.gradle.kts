plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.speechbuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.speechbuddy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.speechbuddy.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.test:monitor:1.6.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20210307")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Lifecycle
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-common:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    // Coroutines
    val coroutinesVersion = "1.7.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Moshi
    val moshiVersion = "1.10.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // RetroFit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.1")

    // Gson
    implementation("com.google.code.gson:gson:2.9.0")

    // DataStore
    val dataStoreVersion = "1.0.0-alpha06"
    implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")
    implementation("androidx.datastore:datastore-preferences-core:$dataStoreVersion")

    // Room
    val roomVersion = "2.6.0"
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Work
    val workVersion = "2.8.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    testImplementation("androidx.work:work-testing:$workVersion")

    // Navigation
    val navigationVersion = "2.7.5"
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.navigation:navigation-runtime-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")

    // Hilt
    val hiltVersion = "2.48.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testAnnotationProcessor("com.google.dagger:hilt-android-compiler:2.44")
    androidTestAnnotationProcessor("com.google.dagger:hilt-android-compiler:2.44")

    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // MockK
    val mockkVersion = "1.13.8"
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-agent:$mockkVersion")
    testImplementation("io.mockk:mockk-android:$mockkVersion")

    // Glide
    val glideVersion = "1.0.0-alpha.3"
    implementation("com.github.bumptech.glide:compose:$glideVersion")

    // multik
    val multikVersion = "0.2.2"
    implementation("org.jetbrains.kotlinx:multik-core:$multikVersion")
    implementation("org.jetbrains.kotlinx:multik-kotlin:$multikVersion")
}