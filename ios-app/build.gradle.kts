/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType


plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

enum class Target(val simulator: Boolean, val key: String) {
    WATCHOS_X86(true, "watchos"),
    WATCHOS_ARM64(false, "watchos"),
    IOS_X64(true, "iosX64"),
    IOS_ARM64(false, "iosArm64")
}

val sdkName: String? = System.getenv("SDK_NAME") ?: "iphonesimulator"

val target = sdkName.orEmpty().let {
    when {
        it.startsWith("iphoneos") -> Target.IOS_ARM64
        it.startsWith("iphonesimulator") -> Target.IOS_X64
        it.startsWith("watchos") -> Target.WATCHOS_ARM64
        it.startsWith("watchsimulator") -> Target.WATCHOS_X86
        else -> throw Error("Unsupported $it")
    }
}

val buildType = System.getenv("CONFIGURATION")?.let {
    NativeBuildType.valueOf(it.toUpperCase())
} ?: NativeBuildType.DEBUG


kotlin {
    val arm64 = iosArm64()
    val x64 = iosX64()

    /*val watchos = if (!target.simulator) {
        // Device.
        watchosArm64("watchos")
    } else {
        // Simulator.
        watchosX86("watchos")
    }*/

    // Declare the output program.
    configure(listOf(arm64, x64)) {
        binaries.executable(listOf(buildType)) {
            baseName = "app"
        }
    }

    /*watchos.binaries.executable(listOf(buildType)) {
        baseName = "watchapp"
    }*/

    //sourceSets["watchosMain"].dependsOn(appleMain)
}

dependencies {
    listOf(
        Deps.Libs.MultiPlatform.kotlinStdLib,
        Deps.Libs.MultiPlatform.coroutines,
        Deps.Libs.MultiPlatform.mokoResources,
        Deps.Libs.MultiPlatform.mokoMvvm,
        Deps.Libs.MultiPlatform.bluefalcon
    ).forEach {
        mppLibrary(it.copy(android = null))
    }

    listOf(
        Modules.MultiPlatform.Feature.mainMap,
        Modules.MultiPlatform.Feature.spotSearch
    ).map { it.name }
        .plus(Modules.MultiPlatform.library)
        .forEach {
            "iosArm64MainImplementation"(project(it))
            "iosX64MainImplementation"(project(it))
        }
}

val xcodeIntegrationGroup: String = "Xcode integration"
val xcodeBundleId = "org.jetbrains.kotlin.native-demo"

val targetBuildDir: String? = System.getenv("TARGET_BUILD_DIR")
val executablePath: String? = System.getenv("EXECUTABLE_PATH")

val currentTarget = kotlin.targets[target.key] as KotlinNativeTarget
val kotlinBinary = currentTarget.binaries.getExecutable(buildType)

val packForXcode = if (sdkName == null || targetBuildDir == null || executablePath == null) {
    // The build is launched not by Xcode ->
    // We cannot create a copy task and just show a meaningful error message.
    tasks.create("packForXCode").doLast {
        throw IllegalStateException("Please run the task from Xcode")
    }
} else {
    // Otherwise copy the executable into the Xcode output directory.
    tasks.create("packForXCode", Copy::class.java) {
        dependsOn(kotlinBinary.linkTask)
        destinationDir = file(targetBuildDir)
        from(kotlinBinary.outputFile)
        rename { executablePath }
    }
}

val xcodeProject = file("ios-app.xcodeproj")
val xcodeAppName = "ios-app"
val xcodeDerivedDataPath = file("$buildDir/xcode-build")

val startSimulator by tasks.creating(Exec::class.java) {
    group = xcodeIntegrationGroup
    description = "Starts an iOS simulator."

    executable = "open"
    args("/Applications/Xcode.app/Contents/Developer/Applications/Simulator.app")
}

val buildAppWithXcode by tasks.creating(Exec::class.java) {
    dependsOn(kotlinBinary.linkTask)
    group = xcodeIntegrationGroup
    description = "Builds the iOS application bundle using Xcode."

    workingDir = xcodeProject
    executable = "sh"
    args(
        "-c", """
        xcrun xcodebuild \
            -scheme $xcodeAppName \
            -project . \
            -configuration Debug \
            -destination 'platform=iOS Simulator,name=iPhone SE,OS=12.2' \
            -derivedDataPath '$xcodeDerivedDataPath' \
            CONFIGURATION_BUILD_DIR='$targetBuildDir'
        """.trimIndent()
    )
}

val installAppInSimulator by tasks.creating(Exec::class.java) {
    dependsOn(buildAppWithXcode)

    group = xcodeIntegrationGroup
    description = "Installs the application bundle on an iOS simulator."
    executable = "sh"

    val appFolder = "${targetBuildDir}/${xcodeAppName}.app"

    args("-c", "xcrun simctl install booted '${appFolder}'")
}

val launchAppInSimulator by tasks.creating(Exec::class.java) {
    dependsOn(installAppInSimulator)

    group = xcodeIntegrationGroup
    description = "Launches the application on an iOS simulator."

    executable = "sh"
    args("-c", "xcrun simctl launch booted $xcodeBundleId")
}

tasks.create("run") {
    group = xcodeIntegrationGroup

    dependsOn(buildAppWithXcode)
    dependsOn(installAppInSimulator)
    dependsOn(launchAppInSimulator)
}
