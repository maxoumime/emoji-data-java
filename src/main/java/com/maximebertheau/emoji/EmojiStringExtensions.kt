package com.maximebertheau.emoji

private val String.bytes get() = split('-').map { it.toInt(16) }.toIntArray()
val String.unicode get() = String(bytes, offset = 0, length = count { it == '-' } + 1)
