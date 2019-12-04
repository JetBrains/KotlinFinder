package com.kotlinconf.library.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers



internal actual val Dispatchers.UI: CoroutineDispatcher get() = Dispatchers.Main