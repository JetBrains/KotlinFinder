# KotlinFinder
## Structure 
- android-app - application for android
- ios-app - application for iOS (in this directory you have ios-app.xcodeproj for Xcode with targets iOS, WatchOS applications)
- watchos-app - kotlin sources of application for WatchOS. target for Xcode contains in ios-app.xcodeproj
- mpp-library - multiplatform library with shared logic across android and ios apps
  - mpp-library:domain - entities, repositories, network api
  - mpp-library:feature:main-map - main screen feature

## Start
Android: Open root directory in Android Studio / IDEA - IDE automatically find run configuration.
iOS: run `pod install` in `ios-app` directory. After complete - open `ios-app/ios-app.xcworkspace`. 
Two schemes - `JetFinder`, `watchapp` use for run application on simulator/device.

## Dependencies
- [Kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) - async
- [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) - json parsing
- [ktor-client](https://github.com/ktorio/ktor) - http client
- [Napier](https://github.com/AAkira/Napier) - logging
- [MOKO resources](https://github.com/icerockdev/moko-resources) - work with resources
- [MOKO mvvm](https://github.com/icerockdev/moko-mvvm) - mvvm architecture components
- [MOKO network](https://github.com/icerockdev/moko-network) - rest api client generation
- [MOKO permissions](https://github.com/icerockdev/moko-permissions) - runtime permissions request
- [blue-falcon](https://github.com/Reedyuk/blue-falcon) - bluetooth scan (used [forked version](https://github.com/icerockdev/blue-falcon))
- [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - local key/value storage

## Authors
Developed by [IceRock](https://github.com/icerockdev) and [JetBrains](http://github.com/jetBrains)