package com.maximebertheau.emoji

data class Emoji(
        val name: String?,
        val unified: UnifiedString,
        val aliases: List<String>,
        val isObsoleted: Boolean,
        val category: Category?,
        val sortOrder: Int,
        val skinVariations: List<SkinVariation>
) {
    val unicode get() = unified.unicode
}