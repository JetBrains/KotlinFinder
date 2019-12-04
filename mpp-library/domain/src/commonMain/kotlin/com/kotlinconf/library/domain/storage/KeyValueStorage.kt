package com.kotlinconf.library.domain.storage

import com.russhwolf.settings.*

class KeyValueStorage(settings: Settings) {
    var isUserRegistered: Boolean by settings.boolean("is_user_registered")
    var cookies: String? by settings.nullableString("cookies")
    var winnerName: String? by settings.nullableString("used_username")
}
