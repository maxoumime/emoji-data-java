package com.maximebertheau.emoji

/**
 * Created by Maxime Bertheau on 5/22/17.
 */
enum class Category(val dataName: String) {
    SMILEYS("Smileys & Emotion"),
    SYMBOLS("Symbols"),
    OBJECTS("Objects"),
    NATURE("Animals & Nature"),
    PEOPLE("People & Body"),
    FOODS("Food & Drink"),
    PLACES("Travel & Places"),
    ACTIVITY("Activities"),
    FLAGS("Flags"),
    SKIN_TONES("Skin Tones");

    companion object {
        @JvmStatic
        fun parse(name: String) = values().firstOrNull { it.dataName == name } ?: throw Error("Unknown category $name")
    }
}
