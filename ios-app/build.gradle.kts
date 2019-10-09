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
    IOS_X64(true, "ios"),
    IOS_ARM64(false, "ios")
}

val sdkName: String? = "iphonesimulator" //System.getenv("SDK_NAME")

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
    // Declare a target.
    // We declare only one target (either arm64 or x64)
    // to workaround lack of common platform libraries
    // for both device and simulator.
    val ios = if (!target.simulator ) {
        // Device.
        iosArm64("ios")
    } else {
        // Simulator.
        iosX64("ios")
    }

    /*val watchos = if (!target.simulator) {
        // Device.
        watchosArm64("watchos")
    } else {
        // Simulator.
        watchosX86("watchos")
    }*/

    // Declare the output program.
    ios.binaries.executable(listOf(buildType)) {
        baseName = "app"
    }

    /*watchos.binaries.executable(listOf(buildType)) {
        baseName = "watchapp"
    }*/

    // Configure dependencies.
    val appleMain by sourceSets.creating {
        dependsOn(sourceSets["commonMain"])
    }

    sourceSets["iosMain"].dependsOn(appleMain)
    //sourceSets["watchosMain"].dependsOn(appleMain)
}

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib.copy(android = null))
   // mppLibrary(Deps.Libs.MultiPlatform.coroutines.copy(android = null))
//    mppLibrary(Deps.Libs.MultiPlatform.mokoResources)
//    mppLibrary(Deps.Libs.MultiPlatform.mokoCore)
}



val xcodeIntegrationGroup: String = "Xcode integration"
val xcodeBundleId = "org.jetbrains.kotlin.native-demo"

val currentTarget = kotlin.targets[target.key] as KotlinNativeTarget
val kotlinBinary = currentTarget.binaries.getExecutable(buildType)

val targetBuildDir: String = "~/Library/Developer/Xcode/DerivedData/ios-app-build-dir/Build/Products/Debug-iphonesimulator"
val executablePath: String = "ios-app.app/ios-app"

val packForXcode by tasks.creating(Copy::class.java) {
    group = xcodeIntegrationGroup

    dependsOn(kotlinBinary.linkTask)
    destinationDir = file(targetBuildDir)
    from(kotlinBinary.outputFile)
    rename { executablePath }
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
    args("-c", """
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
    dependsOn(packForXcode)
    dependsOn(installAppInSimulator)
    dependsOn(launchAppInSimulator)
}