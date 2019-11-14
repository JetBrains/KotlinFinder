import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType


plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    targets {
        val configureIos: KotlinNativeTarget.() -> Unit = {
            binaries {
                framework("MultiPlatformLibrary") {

                }
            }
        }

        watchosArm64("watchosArm64", configureIos)
        watchosX86("watchosX86", configureIos)
    }

    sourceSets {
        val watchosArm64Main by getting {
            kotlin.srcDir(file("src/watchosArm64Main/kotlin"))
        }
        val watchosX86Main by getting {
            kotlin.srcDir(file("src/watchosX86Main/kotlin"))
        }
    }
}

dependencies {
//    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
//    mppLibrary(Deps.Libs.MultiPlatform.coroutines)
}

tasks.mapNotNull { it as? org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink }
    .mapNotNull { it.binary as? org.jetbrains.kotlin.gradle.plugin.mpp.Framework }
    .forEach { framework ->
        val linkTask = framework.linkTask
        val syncTaskName = linkTask.name.replaceFirst("link", "sync")
        val syncFramework = tasks.create(syncTaskName, Sync::class.java) {
            group = "cocoapods"

            val outputDir = file("build/cocoapods/framework")

            from(framework.outputDirectory)
            into(outputDir)

            doLast {
                val file = file("${outputDir.path}/task")
                file.writeText(syncTaskName)
            }
        }
        syncFramework.dependsOn(linkTask)
    }
