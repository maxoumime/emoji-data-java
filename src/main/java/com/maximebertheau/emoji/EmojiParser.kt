package com.maximebertheau.emoji

object EmojiParser {
    private val aliasMatchingRegex = Regex(""":([\w_+-]+)(?:(?:\||::)((type_|skin-tone-\d+)[\w_]*))*:""")
    private val aliasMatchingRegexOptionalColon = Regex(""":?([\w_+-]+)(?:(?:\||::)((type_|skin-tone-\d+)[\w_]*))*:?""")

    /**
     * Converts the emojis in a String to their aliased version.
     * @param input the String potentially containing emoji in their unicode version
     * @return the aliased version of the input
     */
    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    fun parseToAliases(input: String): String {
        if (input.isEmpty()) return input

        val root = EmojiManager.emojiTree.root

        class LastMatch(val node: Node, val path: List<Char>, val char: Char, val index: Int) {
            val emoji = node.emoji!!
        }

        val validMatches = mutableListOf<LastMatch>()

        var node: Node = root
        val path = mutableListOf<Char>()

        fun findFirstNode(c: Char): Node {
            path.clear()
            validMatches.clear()
            return if (root.hasChild(c)) {
                path += c
                root.getChild(c)!!
            } else {
                root
            }
        }

        val matchedEmojis = mutableListOf<Pair<String, Emoji>>()

        var i = 0
        var c: Char? = input[i++]

         while (c != null) {
            if (node.emoji != null && node.hasChild(c)) {
                validMatches.add(LastMatch(
                        node = node,
                        char = input[i-1],
                        path = path,
                        index = i
                ))
            }
            when {
                node.hasChild(c) -> {
                    path += c
                    node = node.getChild(c)!!
                    c = input.getOrNull(i++)
                }
                node.emoji != null -> {
                    matchedEmojis += path.joinToUnicode() to node.emoji!!
                    node = findFirstNode(c)
                    c = input.getOrNull(i++)
                }
                node.emoji == null && validMatches.isNotEmpty() -> {
                    val match = validMatches.last()
                    val oldPath = match.path.dropLast(1).toMutableList()
                    matchedEmojis += oldPath.joinToUnicode() to match.emoji
                    val newPath = path.dropWhile { it == oldPath.removeFirstOrNull() }
                    path.clear()
                    path.addAll(newPath)
                    validMatches.removeLast()

                    node = findNodeForPath(path)!!
                }
                else -> {
                    node = findFirstNode(c)
                    c = input.getOrNull(i++)
                }
            }
        }

        if (node.emoji != null) {
            matchedEmojis += path.joinToUnicode() to node.emoji!!
        }

        return matchedEmojis
                .sortedWith(compareBy { -it.first.length })
//                .sortedWith(compareBy({ -it.key.length }, { -input.indexOf(it.key) }))
//                .sortedByDescending { input.indexOf(it.key) }
                .fold(input) { acc, (toReplace, emoji) ->
//                    val pattern = Pattern.quote(toReplace)
//                    acc.replace(Regex("$pattern\\u200d?"), ":${emoji.aliases.first()}:")
                    acc.replaceFirst(toReplace, ":${emoji.aliases.first()}:")
                }
    }

    private fun List<Char>.joinToUnicode() = joinToString(separator = "")

    @OptIn(ExperimentalStdlibApi::class)
    private fun findNodeForPath(path: List<Char>): Node? {
        val pathfinding = path.toMutableList()
        return generateSequence(EmojiManager.emojiTree.root) { node ->
            pathfinding.removeFirstOrNull()?.let { char ->
                node.takeIf { it.hasChild(char) }?.getChild(char)
            }
        }.drop(1).toList().takeIf { it.count() == path.count() }?.lastOrNull()
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
