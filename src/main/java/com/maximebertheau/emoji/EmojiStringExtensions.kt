package com.maximebertheau.emoji

internal val String.bytes get() = split('-').map { it.toInt(16) }.toIntArray()
internal val String.unicode get() = String(bytes, offset = 0, length = count { it == '-' } + 1)
internal fun String.trimAlias() = trimStart(':').trimEnd(':')
