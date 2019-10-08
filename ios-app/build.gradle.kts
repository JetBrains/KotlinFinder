/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    targets {
        val arm64 = iosArm64()
        val x64 = iosX64()

        configure(listOf(arm64, x64)) {
            binaries {
                executable {
                    baseName = "app"
                }
            }
        }
    }
}

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib.copy(android = null))
    mppLibrary(Deps.Libs.MultiPlatform.coroutines.copy(android = null))
}
