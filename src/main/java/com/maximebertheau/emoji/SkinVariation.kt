package com.maximebertheau.emoji

data class SkinVariation(
        val types: List<SkinVariationType>,
        val unified: UnifiedString
) {
    val unicode get() = unified.unicode
}
