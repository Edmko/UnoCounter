plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = ua.edmko.buildsrc.AndroidSdk.compile

    defaultConfig {
        minSdk = ua.edmko.buildsrc.AndroidSdk.min
        targetSdk = ua.edmko.buildsrc.AndroidSdk.compile
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ua.edmko.buildsrc.ComposeLibraries.Versions.compose
    }
}

dependencies {
    api(ua.edmko.buildsrc.ComposeLibraries.ui)
    api(ua.edmko.buildsrc.ComposeLibraries.uiTooling)
    api(ua.edmko.buildsrc.ComposeLibraries.foundation)
    api(ua.edmko.buildsrc.ComposeLibraries.foundationLayout)
    api(ua.edmko.buildsrc.ComposeLibraries.material)
    api(ua.edmko.buildsrc.ComposeLibraries.iconsCore)
    api(ua.edmko.buildsrc.ComposeLibraries.iconExtended)
    api(ua.edmko.buildsrc.ComposeLibraries.constraint)
}