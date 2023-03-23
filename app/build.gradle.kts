plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.8.10"
  id("kotlin-parcelize")
  id("androidx.navigation.safeargs.kotlin")
  id("dagger.hilt.android.plugin")
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
  namespace = "me.varoa.sad"
  compileSdk = 33

  defaultConfig {
    applicationId = "me.varoa.sad"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }
    getByName("debug") {
      // isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility(JavaVersion.VERSION_11)
    targetCompatibility(JavaVersion.VERSION_11)
  }

  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-opt-in=kotlin.ExperimentalStdlibApi",
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-opt-in=kotlin.RequiresOptIn"
    )
  }

  buildFeatures {
    viewBinding = true
  }
}

kotlin {
  jvmToolchain(11)
}

dependencies {
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
  // coroutine
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

  // core
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.navigation:navigation-runtime-ktx:2.5.3")
  implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
  implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

  // ui
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
  implementation("androidx.paging:paging-runtime-ktx:3.1.1")

  // di
  implementation("com.google.dagger:hilt-android:2.45")
  kapt("com.google.dagger:hilt-android-compiler:2.45")

  // data
  implementation("androidx.datastore:datastore-preferences:1.0.0")

  // gms
  implementation("com.google.android.gms:play-services-maps:18.1.0")
  implementation("com.google.android.gms:play-services-location:21.0.1")

  // settings
  implementation("androidx.preference:preference-ktx:1.2.0")
  implementation("com.afollestad.material-dialogs:core:3.3.0")

  // networking
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

  // other
  implementation("io.coil-kt:coil:2.2.2")
  implementation("com.squareup.logcat:logcat:0.1")
  implementation("id.zelory:compressor:3.0.1")

  // unit testing
  testImplementation("junit:junit:4.13.2")
  testImplementation("io.mockk:mockk:1.13.4")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  testImplementation("app.cash.turbine:turbine:0.12.1")

  // ui testing
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.test:rules:1.5.0")
}