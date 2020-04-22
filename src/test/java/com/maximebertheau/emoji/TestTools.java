//package com.maximebertheau.emoji;
//
//public class TestTools {
//    public static boolean containsEmojis(
//            Iterable<Emoji> emojis,
//            String... aliases
//    ) {
//        for (String alias : aliases) {
//            boolean contains = containsEmoji(emojis, alias);
//            if (!contains) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static boolean containsEmoji(Iterable<Emoji> emojis, String alias) {
//        for (Emoji emoji : emojis) {
//            for (String al : emoji.aliases) {
//                if (alias.equals(al)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//}
