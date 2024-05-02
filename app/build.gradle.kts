plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.langoverse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.langoverse"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        merge("META-INF/NOTICE.md")
        merge("META-INF/LICENSE.md")
    }
}


dependencies {

//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.firebase:firebase-auth:22.3.1")
//    implementation("com.google.firebase:firebase-database:20.3.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation ("com.google.mlkit:translate:17.0.2")
//    implementation ("com.google.mlkit:language-id:17.0.4")
////    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
////    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation ("com.google.android.material:material:1.11.0")
////    implementation ("com.google.firebase:firebase-translate:22.0.0")
//    implementation ("com.google.firebase:firebase-ml-natural-language:28.0.0")
//
//
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")

    implementation(files("libs\\mysql-connector-java-5.1.49.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.mlkit:translate:17.0.2")
    implementation("com.google.mlkit:language-id:17.0.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.sun.mail:android-mail:1.6.6")
    implementation ("com.sun.mail:android-activation:1.6.6")
//        implementation ("com.google.android.gms:play-services-mlkit-translate:17.1.0")

//        implementation("com.google.firebase:firebase-ml-natural-language:28.0.0")
//        implementation ("com.google.firebase:firebase-ml-natural-language-language-id-model:20.0.8")
//        implementation("com.google.firebase:firebase-ml-natural-language-translate-model:20.0.9")



}
