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
     * @return &lt;li&gt;
     * Matches.EXACTLY if char sequence in its entirety is an emoji
     * Matches.POSSIBLY if char sequence matches prefix of an emoji
     * Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an emoji
     */
    fun isEmoji(sequence: CharArray?): Matches {
        sequence ?: return Matches.POSSIBLY

        var tree: Node = root
        for (c in sequence) {
            tree = tree.getChild(c) ?: return Matches.IMPOSSIBLE
        }
        return if (tree.emoji != null) Matches.EXACTLY else Matches.POSSIBLY
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

    enum class Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE;

        fun exactMatch(): Boolean {
            return this == EXACTLY
        }

        fun impossibleMatch(): Boolean {
            return this == IMPOSSIBLE
        }
    }
}