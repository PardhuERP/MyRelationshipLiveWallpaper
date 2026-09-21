plugins {
    id("com.android.application")
}

android {
    namespace = "com.myrelationship.livewallpaper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.myrelationship.livewallpaper"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
