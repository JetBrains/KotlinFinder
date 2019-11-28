/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    repositories {
        jcenter()
        google()

        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }
    }
    resolutionStrategy.eachPlugin {
        // part of plugins defined in Deps.Plugins, part in buildSrc/build.gradle.kts
        val module = Deps.plugins[requested.id.id] ?: return@eachPlugin

        useModule(module)
    }
}

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "JetFinder"

include(":android-app")
include(":mpp-library")
include(":ios-app")
include(":watchos-app")

listOf(
    Modules.MultiPlatform.domain,
    Modules.MultiPlatform.Feature.mainMap
).forEach { include(it.name) }

include(Modules.Android.shared)
