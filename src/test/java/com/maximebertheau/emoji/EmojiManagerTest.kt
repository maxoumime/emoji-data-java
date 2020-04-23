package com.maximebertheau.emoji

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EmojiManagerTest {

    @Test
    fun `getForAlias with unknown alias returns empty`() {
        // GIVEN

        // WHEN
        val emoji = EmojiManager.getForAlias("jkahsgdfjksghfjkshf")

        // THEN

        assertNull(emoji)
    }

    @Test
    fun `getForAlias returns the emoji for the alias`() {
        // GIVEN

        // WHEN
        val emoji = EmojiManager.getForAlias("smile")

        // THEN
        assertNotNull(emoji)
    }

    @Test
    fun `getForAlias with colons returns the emoji for the alias`() {
        // GIVEN

        // WHEN
        val emoji = EmojiManager.getForAlias(":smile:")

        // THEN
        assertNotNull(emoji)
    }

    @Test
    fun `isEmoji for an emoji returns true`() {
        // GIVEN
        val emoji = "😀"

        // WHEN
        val isEmoji = EmojiManager.isEmoji(emoji)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun `isEmoji for a non emoji returns false`() {
        // GIVEN
        val str = "test"

        // WHEN
        val isEmoji = EmojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun `isEmoji for an emoji and other chars returns false`() {
        // GIVEN
        val str = "😀 test"

        // WHEN
        val isEmoji = EmojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun `getAll doesn't return duplicates`() {
        // GIVEN

        // WHEN
        val emojis = EmojiManager.all

        // THEN
        val unicodes = mutableSetOf<String>()
        for (emoji in emojis) {
            assertFalse(
                    "Duplicate: " + emoji.unicode,
                    unicodes.contains(emoji.unicode)
            )
            unicodes.add(emoji.unicode)
        }
        assertEquals(unicodes.size, emojis.size)
    }

    @Test
    fun `getAllCategories returns emojis`() {
        Category.values().forEach { category ->
            val emojis = EmojiManager.getByCategory(category)
            assert(emojis.isNotEmpty())
        }
    }

    @Test
    fun `getAllCategories contains all the emojis`() {
        val emojisInCategoriesCount = Category.values().flatMap { category ->
            EmojiManager.getByCategory(category)
        }.count()

        val allEmojisCount = EmojiManager.all.count()

        assertEquals(allEmojisCount, emojisInCategoriesCount)
    }

    @Test
    fun `getAllCategories only contains pristine emojis`() {
        val emojisInCategories = Category.values().flatMap { category ->
            EmojiManager.getByCategory(category)
        }

        assertTrue(emojisInCategories.any { it.isPristine })
    }

    @Test
    fun `all only contains pristine emojis`() {
        assertTrue(EmojiManager.all.all { it.isPristine })
        assertTrue(EmojiManager.all.any { it.skinVariations.isNotEmpty() })
    }

    @Test
    fun `allWithSkinVariations contains pristine emojis`() {
        assertTrue(EmojiManager.allWithSkinVariations.any { it.isPristine })
    }
}
