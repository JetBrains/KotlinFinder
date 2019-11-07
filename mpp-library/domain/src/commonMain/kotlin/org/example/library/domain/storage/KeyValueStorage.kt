/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.domain.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.int
import com.russhwolf.settings.nullableString

class KeyValueStorage(settings: Settings) {
    var token by settings.nullableString("pref_token")
    var collectedLettersCount: Int by settings.int("collected_letters_count")
    var cookies: String? by settings.nullableString("cookies")
}
