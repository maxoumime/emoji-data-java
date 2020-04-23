package com.maximebertheau.emoji

data class SkinVariation(
        val types: List<SkinVariationType>,
        val unified: String
) {
    val unicode get() = unified.unicode
}
