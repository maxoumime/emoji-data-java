package com.maximebertheau.emoji

import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException

internal object EmojiLoader {

    private const val PATH = "/emojis.json"

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
    internal fun loadEmojis(): Sequence<Emoji> {
        val stream = EmojiLoader::class.java.getResourceAsStream(PATH)

        val json = try {
            stream.use { it.bufferedReader(Charsets.UTF_8).readText() }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

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
                .flatMap { (emojiObject, variation) ->
                    emojiObject.put("unified", variation)
                    emojiObject.toEmojis()
                }
    }

    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    internal fun JSONObject.toEmojis(): Sequence<Emoji> {
        val unified = optString("unified") ?: return emptySequence()
        val aliases = optJSONArray("short_names")?.strings() ?: return emptySequence()
        val category = optString("category")?.let(Category.Companion::parse) ?: return emptySequence()
        val name = optString("name")
        val isObsolete = has("obsoleted_by")
        val sortOrder = optInt("sort_order")

        val skinVariations = optJSONArray("skin_variations")?.arrays()
                ?.map {
                    val types = it.getString(0).split('-').map(SkinVariationType.Companion::fromUnified)
                    val variation = it.getString(1)

                    SkinVariation(types, variation)
                }
                .orEmpty()

        val emojiBase = Emoji(name, unified, aliases, isObsolete, category, sortOrder, skinVariations, isPristine = true)

        return sequence {
            yield(emojiBase)

            skinVariations.forEach { variation ->
                val newAliases = emojiBase.aliases.map { alias ->
                    if (variation.types.isEmpty()) {
                        alias
                    } else {
                        alias + ":" + variation.types.joinToString(separator = "") { ":${it.alias}:" }.trimEnd(':')
                    }
                }
                yield(emojiBase.copy(aliases = newAliases, unified = variation.unified, isPristine = false, skinVariations = emptyList<SkinVariation>()))
            }
        }
    }

    private fun JSONArray.objects() = (0 until length()).map(::getJSONObject)
    private fun JSONArray.arrays() = (0 until length()).map(::getJSONArray)
    private fun JSONArray.strings() = (0 until length()).map(::getString)
    private fun <T> Sequence<T>.reversed() = toList().asReversed().asSequence()
}