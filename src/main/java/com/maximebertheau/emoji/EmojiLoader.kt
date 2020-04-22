package com.maximebertheau.emoji

import org.json.JSONArray
import org.json.JSONObject
import java.io.*

object EmojiLoader {
    /**
     * Loads a JSONArray of emojis from an InputStream, parses it and returns the
     * associated list of [Emoji]s
     *
     * @param stream the stream of the JSONArray
     * @return the list of [Emoji]s
     * @throws IOException if an error occurs while reading the stream or parsing
     * the JSONArray
     */
    @Throws(IOException::class)
    internal fun loadEmojis(stream: InputStream): Sequence<Emoji> {

        val json = stream.use { it.bufferedReader(Charsets.UTF_8).readText() }
        
        return JSONArray(json).objects().asSequence()
                .filter { it.has("unified") }
                .flatMap { emojiObject ->
                    val variations = sequence<String> {
                        yield(emojiObject.getString("unified"))
                        yieldAll(emojiObject.optJSONArray("variations")?.strings().orEmpty())
                    }.reversed()

                    variations.map { variation ->
                        emojiObject to variation
                    }
                }
                .mapNotNull { (emojiObject, variation) ->
                    emojiObject.put("unified", variation)
                    emojiObject.toEmoji()
                }
    }

    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    internal fun JSONObject.toEmoji(): Emoji? {
        val unified = optString("unified")?.let(::UnifiedString) ?: return null
        val aliases = optJSONArray("short_names")?.strings() ?: return null
        val category = optString("category")?.let(Category.Companion::parse)
        val name = optString("name")
        val isObsolete = has("obsoleted_by")
        val sortOrder = optInt("sort_order")

        val skinVariations = optJSONArray("skin_variations")?.arrays()
                ?.map {
                    val types = it.getString(0).split('-').map(SkinVariationType.Companion::fromUnified)
                    val variation = it.getString(1).let(::UnifiedString)

                    SkinVariation(types, variation)
                }
                .orEmpty()

        return Emoji(name, unified, aliases, isObsolete, category, sortOrder, skinVariations)
    }

    private fun JSONArray.objects() = (0 until length()).map(::getJSONObject)
    private fun JSONArray.arrays() = (0 until length()).map(::getJSONArray)
    private fun JSONArray.strings() = (0 until length()).map(::getString)
    private fun <T> Sequence<T>.reversed() = toList().asReversed().asSequence()
}