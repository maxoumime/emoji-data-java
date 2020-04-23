package com.maximebertheau.emoji

data class Emoji(
        val name: String?,
        val unified: String,
        val aliases: List<String>,
        internal val isObsolete: Boolean,
        val category: Category,
        val sortOrder: Int,
        val skinVariations: List<SkinVariation>,
        internal val pristine: Boolean
) {
    val unicode get() = unified.unicode
}