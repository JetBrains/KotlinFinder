/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

allprojects {
    repositories {
        mavenLocal()

        google()
        jcenter()

        maven { url = uri("https://kotlin.bintray.com/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://dl.bintray.com/aakira/maven") }
        maven { url = uri("http://dl.bintray.com/kotlin/kotlin-dev") }
        maven { url = uri("http://dl.bintray.com/kotlin/kotlin-eap") }
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-27170
    configurations.create("compileClasspath")
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}