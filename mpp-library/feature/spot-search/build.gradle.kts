/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlin-android-extensions")
    id("dev.icerock.mobile.multiplatform")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.coroutines)
    mppLibrary(Deps.Libs.MultiPlatform.mokoMvvm)
    mppLibrary(Deps.Libs.MultiPlatform.mokoResources)
    mppLibrary(Deps.Libs.MultiPlatform.bluefalcon)
    mppLibrary(Deps.Libs.MultiPlatform.napier)
    mppModule(Modules.MultiPlatform.domain)

    androidLibrary(Deps.Libs.Android.lifecycle)
}
