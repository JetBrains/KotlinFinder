/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Modules {
    object MultiPlatform {
        const val library = ":mpp-library"

        val domain = MultiPlatformModule(
            name = ":mpp-library:domain",
            exported = true
        )

        val shared = MultiPlatformModule(
            name = ":mpp-library:shared",
            exported = true
        )

        object Feature {
            val mainMap = MultiPlatformModule(
                name = ":mpp-library:feature:main-map",
                exported = true
            )
        }
    }
}