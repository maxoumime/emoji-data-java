package com.maximebertheau.emoji

inline class UnifiedString(private val unified: String) {
    private val bytes get() = unified.split('-').map { it.toInt(16) }.toIntArray()
    val unicode get() = String(bytes, offset = 0, length = unified.count { it == '-' } + 1)
}
