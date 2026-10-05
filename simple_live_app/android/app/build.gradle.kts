import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")

    // The Flutter Gradle Plugin must be applied after the Android and Kotlin plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

/*
 * Android 签名配置
 *
 * GitHub Actions 会在构建前创建：
 *
 * simple_live_app/android/key.properties
 *
 * 内容类似：
 *
 * storePassword=xxxx
 * keyPassword=xxxx
 * keyAlias=simple-live
 * storeFile=/home/runner/work/dart_simple_live/dart_simple_live/release.jks
 */
val keystoreProperties = Properties()

val keystorePropertiesFile = rootProject.file("key.properties")

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.xycz.simple_live"

    compileSdk = flutter.compileSdkVersion

    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        applicationId = "com.xycz.simple_live"

        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion

        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    /*
     * Release 签名
     */
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String

            storeFile = keystoreProperties["storeFile"]
                ?.let { file(it) }

            storePassword = keystoreProperties["storePassword"] as String

            isV1SigningEnabled = true
            isV2SigningEnabled = true
        }
    }

    buildTypes {
        release {
            /*
             * 使用 GitHub Actions 创建的正式签名
             */
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }
}

flutter {
    source = "../.."
}
