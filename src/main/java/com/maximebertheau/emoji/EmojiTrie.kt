package com.maximebertheau.emoji

internal class EmojiTrie(emojis: List<Emoji>) {
    internal val root = Node()

    init {
        for (emoji in emojis) {
            var node = root
            for (c in emoji.unified.unicode.toCharArray()) {
                if (!node.hasChild(c)) {
                    node.addChild(c)
                }
                node = node.getChild(c)!!
            }
            node.emoji = emoji
        }
    }

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or
     * partially.
     * @return true if we have an exact match for this sequence
     */
    fun isEmoji(sequence: CharArray?): Boolean {
        sequence ?: return false

        var tree: Node = root
        for (c in sequence) {
            tree = tree.getChild(c) ?: return false
        }

        return tree.emoji != null
    }

    /**
     * Finds Emoji instance from emoji unicode
     *
     * @param unicode unicode of emoji to get
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    fun getEmoji(unicode: String): Emoji? {
        var tree: Node = root
        for (c in unicode.toCharArray()) {
            tree = tree.getChild(c) ?: return null
        }
        return tree.emoji
    }
}