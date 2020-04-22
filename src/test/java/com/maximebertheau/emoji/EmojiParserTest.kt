package com.maximebertheau.emoji

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class EmojiParserTest {
    @Test
    fun `parseToAliases replaces the emojis by one of their aliases`() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(
                "An :grinning:awesome :smiley:string with a few :wink:emojis!",
                result
        )
    }

    @Test
    fun `parseToAliases with long overlapping emoji`() {
        // GIVEN
        val str = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        //With greedy parsing, this will give :man::woman::boy:
        //THEN
        assertEquals(":man-woman-boy:", result)
    }

    @Test
    fun `parseToAliases continuous non overlapping emojis`() {
        // GIVEN
        val str = "\uD83D\uDC69\uD83D\uDC68\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        //THEN
        assertEquals(":woman::man::boy:", result)
    }

    @Test
    fun `parseToUnicode replaces the aliases and the html by their emoji`() {
        // GIVEN
        val str = "An :grinning:awesome :smiley:string with a few emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("An 😀awesome 😃string with a few emojis!", result)
    }

    @Test
    fun `parseToUnicode with the thumbsup emoji replaces the alias by the emoji`() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Dawesome 😃string with a few 😉emojis!",
                result
        )
    }

    @Test
    fun `parseToUnicode with the thumbsdown emoji replaces the alias by the emoji`() {
        // GIVEN
        val str = "An :-1:awesome :smiley:string with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Eawesome 😃string with a few 😉emojis!",
                result
        )
    }

    @Test
    fun `parseToUnicode with the thumbsup emoji in hex replaces the alias by the emoji`() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Dawesome 😃string with a few 😉emojis!",
                result
        )
    }

    @Test
    fun `parseToUnicode with a fitzpatrick modifier`() {
        // GIVEN
        val str = ":boy::skin-tone-6:"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("\uD83D\uDC66\uD83C\uDFFF", result)
    }

    @Test
    fun `parseToUnicode with an unsupported fitzpatrick modifier doesn't replace`() {
        // GIVEN
        val str = ":grinning::skin-tone-6:"
        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("😀", result)
    }

    @Test
    fun `parseToUnicode with the keycap asterisk emoji replaces the alias by the emoji`() {
        // GIVEN
        val str = "Let's test the :family: emoji and " +
                "its other alias :man-woman-boy:"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("Let's test the \uD83D\uDC6A emoji and its other alias \uD83D\uDC6A", result)
    }

    @Test
    fun `parseToAliases NG and nigeria`() {
        // GIVEN
        val str = "Nigeria is 🇳🇬, NG is 🆖"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals("Nigeria is :flag-ng:, NG is :ng:", result)
    }

    @Test
    fun `parseToAliases couple-kiss woman-woman`() {
        // GIVEN
        val str = "👩‍❤️‍💋‍👩"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":woman-kiss-woman:", result)
    }

    @Test
    fun `parseToAliases emojiv4`() {
        // GIVEN
        val str = "🤤"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":drooling_face:", result)
    }

    @Test
    fun `parseToAliases emojiv4 obsoleted`() {
        // GIVEN
        var str = "\u26F9\uFE0F"

        // WHEN
        var result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":person_with_ball:", result)

        // GIVEN
        str = "\u26F9\uFE0F"

        // WHEN
        result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":person_with_ball:", result)

        // GIVEN
        str = "\u26F9\uFE0F\u200D\u2642\uFE0F"

        // WHEN
        result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":man-bouncing-ball:", result)
    }
}
