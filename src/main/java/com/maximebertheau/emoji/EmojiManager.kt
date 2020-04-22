package com.maximebertheau.emoji

import com.maximebertheau.emoji.EmojiLoader.loadEmojis
import com.maximebertheau.emoji.EmojiTrie.Matches
import java.io.IOException
import java.util.*

object EmojiManager {
    private const val PATH = "/emojis.json"
    private val EMOJIS_BY_ALIAS = mutableMapOf<String, MutableList<Emoji>>()
    private var ALL_EMOJIS: List<Emoji>
    val EMOJI_TREE: EmojiTrie
    private val EMOJIS_BY_CATEGORY = mutableMapOf<Category, MutableList<Emoji>>()

    init {
        val emojis = try {
            EmojiManager::class.java.getResourceAsStream(PATH)
                    .use { loadEmojis(it) }
                    .sortedWith(Comparator { a, b ->
                        when {
                            a.isObsoleted && !b.isObsoleted -> 1
                            !a.isObsoleted && b.isObsoleted -> -1
                            else -> 0
                        }
                    }).toList()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        ALL_EMOJIS = emojis

        val shorterUnicodeFirst = Comparator<Emoji> { a, b -> b.unified.unicode.compareTo(a.unified.unicode) }

        val namesInsertedInCategories = mutableSetOf<String>()
        for (emoji in emojis) {
            // Category map
            if (emoji.category != null && emoji.name != null && !namesInsertedInCategories.add(emoji.name)) {
                val emojiListForCategory = (EMOJIS_BY_CATEGORY[emoji.category] ?: mutableListOf()) + emoji
                EMOJIS_BY_CATEGORY[emoji.category] = emojiListForCategory.sortedBy { it.sortOrder }.toMutableList()
            }

            // Alias map
            for (alias in emoji.aliases) {
                val emojiList = (EMOJIS_BY_ALIAS[alias] ?: mutableListOf()) + emoji
                EMOJIS_BY_ALIAS[alias] = emojiList.sortedWith(shorterUnicodeFirst).toMutableList()
            }
        }
        EMOJI_TREE = EmojiTrie(emojis)
    }

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     * @return the associated [Emoji], null if the alias
     * is unknown
     */
    @JvmStatic
    fun getForAlias(alias: String?): List<Emoji> {
        alias ?: return emptyList()
        return EMOJIS_BY_ALIAS[trimAlias(alias)].orEmpty()
    }

    private fun trimAlias(alias: String) = alias.trimStart(':').trimEnd(':')

    /**
     * Returns the [Emoji] for a given unicode.
     *
     * @param unicode the the unicode
     * @return the associated [Emoji], null if the
     * unicode is unknown
     */
    @JvmStatic
    fun getByUnicode(unicode: String?): Emoji? {
        unicode ?: return null
        return EMOJI_TREE.getEmoji(unicode)
    }

    /**
     * Returns all the [Emoji]s
     *
     * @return all the [Emoji]s
     */
    @JvmStatic
    val all: Collection<Emoji> get() = ALL_EMOJIS

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     * @return true if the string is an emoji's unicode, false else
     */
    fun isEmoji(string: String?): Boolean {
        return string != null && EMOJI_TREE.isEmoji(string.toCharArray()).exactMatch()
    }

    @JvmStatic
    fun getByCategory(category: Category) = EMOJIS_BY_CATEGORY[category].orEmpty()

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or
     * partially.
     * @return &lt;li&gt;
     * Matches.EXACTLY if char sequence in its entirety is an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.POSSIBLY if char sequence matches prefix of an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an
     * emoji
     * &lt;/li&gt;
     */
    @JvmStatic
    fun isEmoji(sequence: CharArray?): Matches = EMOJI_TREE.isEmoji(sequence)
}