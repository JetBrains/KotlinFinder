# structure 
- android-app - application for android
- ios-app - application for iOS (in this directory you have ios-app.xcodeproj for Xcode with targets iOS, WatchOS applications)
- watchos-app - kotlin sources of application for WatchOS. target for Xcode contains in ios-app.xcodeproj
- mpp-library - multiplatform library with shared logic across android and ios apps
  - mpp-library:domain - entities, repositories, network api
  - mpp-library:feature:main-map - main screen feature

# start
Android: Open root directory in Android Studio / IDEA - IDE automatically find run configuration.
iOS: run `pod install` in `ios-app` directory. After complete - open `ios-app/ios-app.xcworkspace`. 
Two schemes - `JetFinder`, `watchapp` use for run application on simulator/device.
