package com.maximebertheau.emoji

/**
 * Enum that represents the Fitzpatrick modifiers supported by the emojis.
 */
enum class SkinVariationType(val unified: String) {
    /**
     * Fitzpatrick modifier of type 1/2 (pale white/white)
     */
    TYPE_1_2("1F3FB"),

    /**
     * Fitzpatrick modifier of type 3 (cream white)
     */
    TYPE_3("1F3FC"),

    /**
     * Fitzpatrick modifier of type 4 (moderate brown)
     */
    TYPE_4("1F3FD"),

    /**
     * Fitzpatrick modifier of type 5 (dark brown)
     */
    TYPE_5("1F3FE"),

    /**
     * Fitzpatrick modifier of type 6 (black)
     */
    TYPE_6("1F3FF");

    companion object {
        @JvmStatic
        fun fromUnified(unified: String) = values().firstOrNull { it.unified == unified } ?: throw Error("$unified doesn't exist!")
        fun fromAlias(alias: String): SkinVariationType? {
            return when (alias) {
                "skin-tone-1", "type_1" -> TYPE_1_2
                "skin-tone-2", "type_2" -> TYPE_1_2
                "skin-tone-3", "type_3" -> TYPE_3
                "skin-tone-4", "type_4" -> TYPE_4
                "skin-tone-5", "type_5" -> TYPE_5
                "skin-tone-6", "type_6" -> TYPE_6
                else -> null
            }
        }
    }
}