package com.maximebertheau.emoji;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class EmojiManager {
    private static final String PATH = "/emojis.json";
    private static final Map<String, Emoji> EMOJIS_BY_ALIAS =
            new HashMap<String, Emoji>();
    private static final List<Emoji> ALL_EMOJIS;
    private static final EmojiTrie EMOJI_TRIE;

    static {
        try {
            InputStream stream = EmojiLoader.class.getResourceAsStream(PATH);
            List<Emoji> emojis = EmojiLoader.loadEmojis(stream);
            ALL_EMOJIS = emojis;
            for (Emoji emoji : emojis) {
                for (String alias : emoji.getAliases()) {
                    EMOJIS_BY_ALIAS.put(alias, emoji);
                }
            }

            EMOJI_TRIE = new EmojiTrie(emojis);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * No need for a constructor, all the methods are static.
     */
    private EmojiManager() {
    }

    /**
     * Returns the {@link Emoji} for a given alias.
     *
     * @param alias the alias
     * @return the associated {@link Emoji}, null if the alias
     * is unknown
     */
    public static Emoji getForAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return EMOJIS_BY_ALIAS.get(trimAlias(alias));
    }

    private static String trimAlias(String alias) {
        String result = alias;
        if (result.startsWith(":")) {
            result = result.substring(1, result.length());
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }


    /**
     * Returns the {@link Emoji} for a given unicode.
     *
     * @param unicode the the unicode
     * @return the associated {@link Emoji}, null if the
     * unicode is unknown
     */
    public static Emoji getByUnicode(String unicode) {
        if (unicode == null) {
            return null;
        }
        return EMOJI_TRIE.getEmoji(unicode);
    }

    /**
     * Returns all the {@link Emoji}s
     *
     * @return all the {@link Emoji}s
     */
    public static Collection<Emoji> getAll() {
        return ALL_EMOJIS;
    }

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     * @return true if the string is an emoji's unicode, false else
     */
    public static boolean isEmoji(String string) {
        return string != null &&
                EMOJI_TRIE.isEmoji(string.toCharArray()).exactMatch();
    }

    /**
     * Tests if a given String only contains emojis.
     *
     * @param string the string to test
     * @return true if the string only contains emojis, false else
     */
    public static boolean isOnlyEmojis(String string) {
        return string != null && EmojiParser.removeAllEmojis(string).isEmpty();
    }

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or
     *                 partially.
     * @return &lt;li&gt;
     * Matches.EXACTLY if char sequence in its entirety is an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.POSSIBLY if char sequence matches prefix of an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an
     * emoji
     * &lt;/li&gt;
     */
    public static EmojiTrie.Matches isEmoji(char[] sequence) {
        return EMOJI_TRIE.isEmoji(sequence);
    }
}
