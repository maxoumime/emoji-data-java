package com.maximebertheau.emoji

data class Emoji(
        val name: String?,
        val unified: String,
        val aliases: List<String>,
        internal val isObsoleted: Boolean,
        val category: Category,
        val sortOrder: Int,
        val skinVariations: List<SkinVariation>
) {
    val unicode get() = unified.unicode
}