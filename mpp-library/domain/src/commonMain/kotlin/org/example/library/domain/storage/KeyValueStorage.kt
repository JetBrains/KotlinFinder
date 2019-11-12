/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.domain.storage

import com.russhwolf.settings.*

class KeyValueStorage(settings: Settings) {
    var isUserRegistered: Boolean by settings.boolean("is_user_registered")
    var collectedSpotIdsStr: String? by settings.nullableString("collected_spot_ids")
    var cookies: String? by settings.nullableString("cookies")
}
