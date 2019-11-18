package org.example.library.domain.storage

import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url


class PersistentCookiesStorage(
    private val storage: KeyValueStorage
): CookiesStorage {
    private val cookies: MutableMap<String, String>
    private var lastCookie: String? = null
    private val bannedCookies: MutableSet<String> = mutableSetOf()

    init {
        this.cookies = this.cookiesFromStorage()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        return this.cookies.map{ Cookie(name = it.key, value = it.value) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit {
        if (this.bannedCookies.contains(cookie.value))
            return

        this.cookies.set(key = cookie.name, value = cookie.value)
        this.lastCookie = cookie.value

        this.storage.cookies = this.stringFromCookies(this.cookies)
    }

    override fun close() {}

    fun banCookie() {
        val cookie: String = this.lastCookie ?: return

        this.bannedCookies.add(cookie)

        this.cookies.clear()
        this.storage.cookies = null
    }

    fun lastCookie(): String? {
        return this.lastCookie
    }

    private fun cookiesFromStorage(): MutableMap<String, String> {
        val cookiesStr: String = this.storage.cookies ?: return mutableMapOf()
        val pairs: List<String> = cookiesStr.split(",")

        val cookies: MutableMap<String, String> = mutableMapOf()

        for (p: String in pairs) {
            val cookie: List<String> = p.split(":")

            if (cookie.count() == 2) {
                cookies.set(key = cookie[0], value = cookie[1])
            }
        }

        return cookies
    }

    private fun stringFromCookies(cookies: Map<String, String>): String {
        var str: String = ""

        for ((k, v) in cookies) {
            str = "$str,$k:$v"
        }

        return str
    }
}