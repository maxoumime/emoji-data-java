package com.maximebertheau.emoji

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class EmojiParserTest {

    @Test
    fun `can parse all the emojis from their alias version`() {
        // GIVEN
        val str = EmojiManager.all.flatMap { it.aliases }.joinToString(separator = "") { ":$it:" }

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assert(!result.contains(Regex("""[A-Za-z:]""")))
    }

    @Test
    fun `can parse all the emojis from their unicode version`() {
        // GIVEN
        val emojis = EmojiManager.all
        val str = emojis.joinToString(separator = "") { it.unified.unicode }

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        val aliasVersion = emojis.map { it.aliases.first() }.joinToString(separator = "") { ":$it:" }
        assertEquals(aliasVersion, result)
    }

    @Test
    fun test() {
        val unicode = EmojiParser.parseToUnicode(":hash::keycap_star::zero::one::two::three::four::five::six::seven::eight::nine::copyright::registered::mahjong::black_joker::a::b::o2::parking::ab::cl::cool::free::id::new::ng::ok::sos::up::vs::flag-ac::flag-ad::flag-ae::flag-af::flag-ag::flag-ai::flag-al::flag-am::flag-ao::flag-aq::flag-ar::flag-as::flag-at::flag-au::flag-aw::flag-ax::flag-az::flag-ba::flag-bb::flag-bd::flag-be::flag-bf::flag-bg::flag-bh::flag-bi::flag-bj::flag-bl::flag-bm::flag-bn::flag-bo::flag-bq::flag-br::flag-bs::flag-bt::flag-bv::flag-bw::flag-by::flag-bz::flag-ca::flag-cc::flag-cd:")

        val result = EmojiParser.parseToAliases(unicode)

    }

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

    @Test
    fun `from unicode, can parse emojis with multiple skin colors`() {
        // GIVEN
        val str = "\uD83D\uDC68\uD83C\uDFFF\u200D\uD83E\uDD1D\u200D\uD83D\uDC68\uD83C\uDFFB"

        // WHEN

        val aliases = EmojiParser.parseToAliases(str)

        // THEN

        assertEquals(":two_men_holding_hands::skin-tone-6::skin-tone-2:", aliases)
    }

    @Test
    fun `from aliases, can parse emojis with multiple skin colors`() {
        // GIVEN
        val str = ":two_men_holding_hands::skin-tone-6::skin-tone-2:"

        // WHEN

        val unicode = EmojiParser.parseToUnicode(str)

        // THEN

        assertEquals("\uD83D\uDC68\uD83C\uDFFF\u200D\uD83E\uDD1D\u200D\uD83D\uDC68\uD83C\uDFFB", unicode)
    }

    @Test
    fun `from unicode, can parse emojis with multiple skin colors back and forth`() {
        // GIVEN
        val str = "\uD83D\uDC68\uD83C\uDFFF\u200D\uD83E\uDD1D\u200D\uD83D\uDC68\uD83C\uDFFB"

        // WHEN

        val aliases = EmojiParser.parseToAliases(str)
        val reEmoji = EmojiParser.parseToUnicode(aliases)

        // THEN

        assertEquals(str, reEmoji)
    }

    @Test
    fun `from aliases, can parse emojis with multiple skin colors back and forth`() {
        // GIVEN
        val str = ":two_men_holding_hands::skin-tone-6::skin-tone-2:"

        // WHEN

        val unicode = EmojiParser.parseToUnicode(str)
        val aliases = EmojiParser.parseToAliases(unicode)

        // THEN

        assertEquals(str, aliases)
    }
}
