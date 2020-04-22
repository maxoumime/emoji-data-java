package com.maximebertheau.emoji

import java.util.regex.Pattern

object EmojiParser {
    private val aliasMatchingRegex = Regex(""":([\w_+-]+)(?:(?:\||::)((type_|skin-tone-\d+)[\w_]*))*:""")
    private val aliasMatchingRegexOptionalColon = Regex(""":?([\w_+-]+)(?:(?:\||::)((type_|skin-tone-\d+)[\w_]*))*:?""")

    /**
     * Converts the emojis in a String to their aliased version.
     * @param input the String potentially containing emoji in their unicode version
     * @return the aliased version of the input
     */
    @JvmStatic
    fun parseToAliases(input: String): String {
        if (input.isEmpty()) return input

        val root = EmojiManager.emojiTree.root

        var node: Node = root
        var lastValidNode: Node? = null
        var lastValidPath = mutableListOf<Char>()
        var path = mutableListOf<Char>()

        fun findFirstNode(c: Char): Node {
            path = mutableListOf()
            lastValidNode = null
            lastValidPath = mutableListOf()
            return if (root.hasChild(c)) {
                path.add(c)
                root.getChild(c)!!
            } else {
                root
            }
        }

        val thrownAwayChars = mutableListOf<Char>()
        val thrownAwayPaths = mutableListOf<String>()

        val matchedEmojis = mutableMapOf<String, Emoji>()

        for (c in input) {
            if (node.emoji != null && node.hasChild(c)) {
                lastValidPath = path
                lastValidNode = node
            }
            when {
                node.hasChild(c) -> {
                    path.add(c)
                    node = node.getChild(c)!!
                }
                node.emoji != null -> {
                    matchedEmojis[path.joinToString(separator = "")] = node.emoji!!
                    node = findFirstNode(c)
                }
                node.emoji == null -> {
                    lastValidNode?.emoji?.let {
                        matchedEmojis[lastValidPath.joinToString(separator = "")] = it

                        thrownAwayChars += path - lastValidPath + c
                        thrownAwayPaths += (lastValidPath + c).joinToString(separator = "")
                    }

                    node = findFirstNode(c)
                }
            }
        }

        lastValidNode?.emoji?.let {
            matchedEmojis[lastValidPath.joinToString(separator = "")] = it
        }

        if (node.emoji != null) {
            matchedEmojis[path.joinToString(separator = "")] = node.emoji!!
        }

        return matchedEmojis.entries
                .sortedByDescending { it.key.length }
                .fold(input) { acc, (toReplace, emoji) ->
                    val pattern = Pattern.quote(toReplace)
                    acc.replace(Regex("$pattern\\u200d?"), ":${emoji.aliases.first()}:")
                }
    }

    /**
     * Returns the unicode version of the aliased emojis in a String
     * @param input the String potentially containing aliased emojis
     * @return the transformed String
     */
    @JvmStatic
    fun parseToUnicode(input: String): String {
        return input.getUnicodesForAliases().entries.fold(input) { acc, (alias, emoji) ->
            acc.replace(alias, emoji)
        }
    }

    private fun String.getUnicodesForAliases(): Map<String, String> {
        val input = this
        val results = aliasMatchingRegex.findAll(input)

        if (results.none()) return emptyMap()

        val uniqueMatches = mutableMapOf<String, String>()

        results.forEach { result ->
            val fullAlias = input.substring(result.range)

            if (uniqueMatches.containsKey(fullAlias)) return@forEach

            val matchingUnicode = getUnicodeFromAlias(fullAlias)

            if (matchingUnicode != null) {
                uniqueMatches[fullAlias] = matchingUnicode
            } else {
                // If we can't convert this alias, it's because a skin tone was added and this emoji doesn't support
                // skin tone modifiers
                fullAlias.split("::")
                        .forEach fallback@ {
                            val alias = ":${it.trimAlias()}:"
                            uniqueMatches[alias] = getUnicodeFromAlias(alias) ?: return@fallback
                        }
            }
        }

        return uniqueMatches.entries // Cannot sort a Map directly (toSortedMap doesn't work for odd counts)
                .sortedByDescending { it.value.length } // Execute the longer first so emojis with skin variations are executed before the ones without
                .map { it.key to it.value }
                .toMap()
    }

    private fun getUnicodeFromAlias(input: String): String? {
        val results = aliasMatchingRegexOptionalColon.findAll(input)

        if (results.none()) return null

        val match = results.first()

        val aliasMatch = match.groups.firstOrNull() ?: return null
        val alias = input.substring(aliasMatch.range)

        val emoji = EmojiManager.getForAlias(alias) ?: return null

        return emoji.unified.unicode
    }
}
