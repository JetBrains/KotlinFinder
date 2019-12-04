

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()

    maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    maven { url = uri("http://dl.bintray.com/kotlin/kotlin-dev") }
    maven { url = uri("http://dl.bintray.com/kotlin/kotlin-eap") }
}

dependencies {
    implementation("dev.icerock:mobile-multiplatform:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.60")
    implementation("com.android.tools.build:gradle:3.5.2")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
