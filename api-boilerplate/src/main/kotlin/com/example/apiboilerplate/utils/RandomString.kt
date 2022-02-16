package com.example.apiboilerplate.utils

import java.security.SecureRandom
import java.util.*

class RandomString(
    length: Int,
    private var symbols: String = alphanum
) {

    private var buf: CharArray = CharArray(length)
    private var random: Random = SecureRandom()

    init {
        require(length >= 1)
        require(symbols.length >= 2)
    }

    /**
     * Generate a random string.
     */
    fun nextString(): String {
        for (idx in buf.indices) buf[idx] = symbols[random.nextInt(symbols.length)]
        return String(buf)
    }

    companion object {
        private const val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val lower = upper.lowercase()
        private const val digits = "0123456789"
        private val alphanum = upper + lower + digits
    }
}