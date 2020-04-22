//package com.maximebertheau.emoji;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
//@RunWith(JUnit4.class)
//public class EmojiManagerTest {
//    @Test
//    public void getForAlias_with_unknown_alias_returns_null() throws IOException {
//        // GIVEN
//
//        // WHEN
//        List<Emoji> emojis = EmojiManager.getForAlias("jkahsgdfjksghfjkshf");
//
//        // THEN
//        assertNull(emojis);
//    }
//
//    @Test
//    public void getForAlias_returns_the_emoji_for_the_alias() throws IOException {
//        // GIVEN
//
//        // WHEN
//        List<Emoji> emojis = EmojiManager.getForAlias("smile");
//
//        // THEN
//        assertNotNull(emojis);
//    }
//
//    @Test
//    public void getForAlias_with_colons_returns_the_emoji_for_the_alias()
//            throws IOException {
//        // GIVEN
//
//        // WHEN
//        List<Emoji> emojis = EmojiManager.getForAlias(":smile:");
//
//        // THEN
//        assertNotNull(emojis);
//    }
//
//    @Test
//    public void isEmoji_for_an_emoji_returns_true() {
//        // GIVEN
//        String emoji = "😀";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isEmoji(emoji);
//
//        // THEN
//        assertTrue(isEmoji);
//    }
//
//    @Test
//    public void isEmoji_for_a_non_emoji_returns_false() {
//        // GIVEN
//        String str = "test";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isEmoji(str);
//
//        // THEN
//        assertFalse(isEmoji);
//    }
//
//    @Test
//    public void isEmoji_for_an_emoji_and_other_chars_returns_false() {
//        // GIVEN
//        String str = "😀 test";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isEmoji(str);
//
//        // THEN
//        assertFalse(isEmoji);
//    }
//
//    @Test
//    public void isOnlyEmojis_for_an_emoji_returns_true() {
//        // GIVEN
//        String str = "😀";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
//
//        // THEN
//        assertTrue(isEmoji);
//    }
//
//    @Test
//    public void isOnlyEmojis_for_emojis_returns_true() {
//        // GIVEN
//        String str = "😀😀😀";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
//
//        // THEN
//        assertTrue(isEmoji);
//    }
//
//    @Test
//    public void isOnlyEmojis_for_random_string_returns_false() {
//        // GIVEN
//        String str = "😀a";
//
//        // WHEN
//        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
//
//        // THEN
//        assertFalse(isEmoji);
//    }
//
//    @Test
//    public void getAll_doesnt_return_duplicates() {
//        // GIVEN
//
//        // WHEN
//        Collection<Emoji> emojis = EmojiManager.getAll();
//
//        // THEN
//        Set<String> unicodes = new HashSet<String>();
//        for (Emoji emoji : emojis) {
//            assertFalse(
//                    "Duplicate: " + emoji.getUnicode(),
//                    unicodes.contains(emoji.getUnicode())
//            );
//            unicodes.add(emoji.getUnicode());
//        }
//        assertEquals(unicodes.size(), emojis.size());
//    }
//
//    @Test
//    public void getAllCategories() {
//        for (Category category : Category.values()) {
//            List<Emoji> emojis = EmojiManager.getByCategory(category);
//            assertNotNull(emojis);
//        }
//    }
//}
