package com.maximebertheau.emoji

internal class Node(private val char: Char? = null) {
    private val children = mutableMapOf<Char, Node>()

    var emoji: Emoji? = null

    fun hasChild(child: Char): Boolean = children.containsKey(child)

    fun addChild(child: Char) {
        children[child] = Node(child)
    }

    fun getChild(child: Char): Node? = children[child]

    override fun toString(): String {
        return """(${emoji?.unicode}) | $char -> [${children.map { it.key.toInt().toString(16).toUpperCase() }}]"""
    }
}
