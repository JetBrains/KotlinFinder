/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.domain.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.int
import com.russhwolf.settings.nullableString
import com.russhwolf.settings.string

class KeyValueStorage(settings: Settings) {
    var token by settings.nullableString("pref_token")
    var collectedSpotIdsStr: String? by settings.nullableString("collected_spot_ids")
}
