package com.example.apiboilerplate.utils

import java.security.SecureRandom
import java.util.*

class RandomString(
    private val length: Int = 21,
    private var random: Random = SecureRandom(),
    private var symbols: String = alphanum,
    private var buf: CharArray = CharArray(length)
) {

    init {
        require(length >= 1)
        require(symbols.length >= 2)
        this.random = Objects.requireNonNull(random)
        this.symbols = symbols.toCharArray().toString()
        this.buf = CharArray(length)
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