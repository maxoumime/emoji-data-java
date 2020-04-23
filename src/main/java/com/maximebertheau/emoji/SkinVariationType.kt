package com.maximebertheau.emoji

/**
 * Enum that represents the Fitzpatrick modifiers supported by the emojis.
 */
enum class SkinVariationType(val unified: String, val alias: String) {
    /**
     * Fitzpatrick modifier of type 1/2 (pale white/white)
     */
    TYPE_1_2("1F3FB", "skin-tone-2"),

    /**
     * Fitzpatrick modifier of type 3 (cream white)
     */
    TYPE_3("1F3FC", "skin-tone-3"),

    /**
     * Fitzpatrick modifier of type 4 (moderate brown)
     */
    TYPE_4("1F3FD", "skin-tone-4"),

    /**
     * Fitzpatrick modifier of type 5 (dark brown)
     */
    TYPE_5("1F3FE", "skin-tone-5"),

    /**
     * Fitzpatrick modifier of type 6 (black)
     */
    TYPE_6("1F3FF", "skin-tone-6");

    companion object {
        @JvmStatic
        fun fromUnified(unified: String) = values().firstOrNull { it.unified == unified }
                ?: throw Error("$unified doesn't exist!")

        @JvmStatic
        fun fromAlias(alias: String): SkinVariationType? = values().firstOrNull { it.alias == alias }
    }
}