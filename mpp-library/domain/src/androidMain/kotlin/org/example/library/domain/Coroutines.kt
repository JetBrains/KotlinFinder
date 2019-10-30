package org.example.library.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

internal actual val Dispatchers.UI: CoroutineDispatcher get() = Dispatchers.Main