package com.maximebertheau.emoji

class EmojiTrie(emojis: List<Emoji>) {
    val root = Node()

    init {
        for (emoji in emojis) {
            var tree: Node = root
            for (c in emoji.unified.unicode.toCharArray()) {
                if (!tree.hasChild(c)) {
                    tree.addChild(c)
                }
                tree = tree.getChild(c)!!
            }
            tree.emoji = emoji
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
        return if (tree.isEndOfEmoji) Matches.EXACTLY else Matches.POSSIBLY
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

    inner class Node {
        private val children = mutableMapOf<Char, Node>()

        var emoji: Emoji? = null

        val hasChildren get() = children.isNotEmpty()
        fun hasChild(child: Char): Boolean = children.containsKey(child)

        fun addChild(child: Char) {
            children[child] = Node()
        }

        fun getChild(child: Char): Node? = children[child]

        val isEndOfEmoji: Boolean get() = emoji != null
    }
}