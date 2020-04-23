package com.maximebertheau.emoji

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

        class LastMatch(val node: Node, val path: List<Char>) {
            val emoji = node.emoji!!
        }

        val validMatches = mutableListOf<LastMatch>()

        var node: Node = root
        val path = mutableListOf<Char>()

        fun newPathFinding(c: Char): Node {
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

            // Sometimes we go too far during the path finding, we need to keep track of checkpoints where we did match
            // and emoji
            if (node.emoji != null && node.hasChild(c)) {
                validMatches += LastMatch(node = node, path = path)
            }

            when {
                // Go further down the rabbit hole
                node.hasChild(c) -> {
                    path += c
                    node = node.getChild(c)!!
                    c = input.getOrNull(i++)
                }
                // Add the new emoji
                node.emoji != null -> {
                    matchedEmojis += path.joinToUnicode() to node.emoji!!
                    node = newPathFinding(c)
                    c = input.getOrNull(i++)
                }
                // We went too far, check if we got a match before
                node.emoji == null && validMatches.isNotEmpty() -> {

                    // Get the last match
                    val match = validMatches.last()

                    // Remove the last iteration, that's where we got it wrong
                    val oldPath = match.path.dropLast(1).toMutableList()

                    // Add the new match
                    matchedEmojis += oldPath.joinToUnicode() to match.emoji

                    // Rewinding time...
                    val newPath = path.dropWhile { char ->
                        char == oldPath.takeIf { it.isNotEmpty() }?.removeAt(0)
                    }
                    path.clear()
                    path.addAll(newPath)
                    node = findNodeForPath(path)!!
                    validMatches.removeAt(validMatches.lastIndex)
                }
                // No match at all, move to the next character
                else -> {
                    node = newPathFinding(c)
                    c = input.getOrNull(i++)
                }
            }
        }

        if (node.emoji != null) {
            matchedEmojis += path.joinToUnicode() to node.emoji!!
        }

        return matchedEmojis.fold(input) { acc, (toReplace, emoji) ->
            acc.replaceFirst(toReplace, ":${emoji.aliases.first()}:")
        }
    }

    private fun List<Char>.joinToUnicode() = joinToString(separator = "")

    private fun findNodeForPath(path: List<Char>): Node? {
        return generateSequence(EmojiManager.emojiTree.root to path) { (node, steps) ->
            val nextStep = steps.firstOrNull() ?: return@generateSequence null
            node.takeIf { it.hasChild(nextStep) }?.getChild(nextStep)?.to(steps.drop(1))
        }
                .drop(1) // Removing the first iteration (initial values)
                .takeIf { it.count() == path.count() }
                ?.lastOrNull()
                ?.first
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
                        .forEach fallback@{
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
