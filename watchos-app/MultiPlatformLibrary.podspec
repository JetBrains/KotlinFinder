Pod::Spec.new do |spec|
    spec.name                     = 'MultiPlatformLibrary'
    spec.version                  = '0.1.0'
    spec.homepage                 = 'Link to a Kotlin/Native module homepage'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = 'IceRock Development'
    spec.license                  = ''
    spec.summary                  = 'Shared code between iOS and Android'

    spec.vendored_frameworks      = "build/cocoapods/framework/#{spec.name}.framework"
    spec.libraries                = "c++"
    spec.module_name              = "#{spec.name}_umbrella"

    spec.pod_target_xcconfig = {
        'MPP_LIBRARY_NAME' => 'MultiPlatformLibrary',

        'GRADLE_WATCHOS_TASK_32[config=*ebug]' => 'linkMultiPlatformLibraryDebugFrameworkWatchosArm32',
        'GRADLE_WATCHOS_TASK_32[config=*elease]' => 'linkMultiPlatformLibraryReleaseFrameworkWatchosArm32',
        'GRADLE_WATCHOS_TASK_64[config=*ebug]' => 'linkMultiPlatformLibraryDebugFrameworkWatchosArm64',
        'GRADLE_WATCHOS_TASK_64[config=*elease]' => 'linkMultiPlatformLibraryReleaseFrameworkWatchosArm64',

        'WATCHOS_LIBRARY_FOLDER_32[config=*elease]' => 'watchosArm32/MultiPlatformLibraryReleaseFramework',
        'WATCHOS_LIBRARY_FOLDER_32[config=*ebug]' => 'watchosArm32/MultiPlatformLibraryDebugFramework',
        'WATCHOS_LIBRARY_FOLDER_64[config=*elease]' => 'watchosArm64/MultiPlatformLibraryReleaseFramework',
        'WATCHOS_LIBRARY_FOLDER_64[config=*ebug]' => 'watchosArm64/MultiPlatformLibraryDebugFramework'
    }

    spec.script_phases = [
        {
            :name => 'Compile Kotlin/Native',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
MPP_PROJECT_ROOT="$SRCROOT/../../watchos-app"

MPP_OUTPUT_DIR="$MPP_PROJECT_ROOT/build/cocoapods/framework"
MPP_OUTPUT_NAME="$MPP_OUTPUT_DIR/#{spec.name}.framework"
"$MPP_PROJECT_ROOT/../gradlew" -p "$MPP_PROJECT_ROOT" "$GRADLE_WATCHOS_TASK_32"
"$MPP_PROJECT_ROOT/../gradlew" -p "$MPP_PROJECT_ROOT" "$GRADLE_WATCHOS_TASK_64"

MPP_OUTPUT_32="$MPP_PROJECT_ROOT/build/bin/$WATCHOS_LIBRARY_FOLDER_32/MultiPlatformLibrary.framework/MultiPlatformLibrary"
MPP_OUTPUT_64="$MPP_PROJECT_ROOT/build/bin/$WATCHOS_LIBRARY_FOLDER_64/MultiPlatformLibrary.framework/MultiPlatformLibrary"

lipo -create "$MPP_OUTPUT_32" "$MPP_OUTPUT_64" -output "$MPP_OUTPUT_NAME/MultiPlatformLibrary"
            SCRIPT
        }
    ]
end
