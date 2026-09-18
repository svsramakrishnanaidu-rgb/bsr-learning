plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("com.google.gms.google-services") }
android { namespace="org.beacon.companion"; compileSdk=35
 defaultConfig { applicationId="org.beacon.companion"; minSdk=26; targetSdk=35; versionCode=1; versionName="1.0"; buildConfigField("String","API_BASE_URL","\"https://example.invalid\"") }
 buildFeatures { buildConfig=true }
}
dependencies { implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.work:work-runtime-ktx:2.10.0"); implementation("com.google.android.gms:play-services-location:21.3.0"); implementation("com.google.firebase:firebase-messaging-ktx:24.1.0"); implementation("com.squareup.okhttp3:okhttp:4.12.0"); implementation("androidx.security:security-crypto:1.1.0-alpha06") }
