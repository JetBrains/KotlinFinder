

object Modules {
    object Android {
        const val shared = ":mpp-library:shared"
    }

    object MultiPlatform {
        const val library = ":mpp-library"

        val domain = MultiPlatformModule(
            name = ":mpp-library:domain",
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