package com.maximebertheau.emoji;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads the emojis from a JSON database.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class EmojiLoader {
    /**
     * No need for a constructor, all the methods are static.
     */
    private EmojiLoader() {
    }

    /**
     * Loads a JSONArray of emojis from an InputStream, parses it and returns the
     * associated list of {@link Emoji}s
     *
     * @param stream the stream of the JSONArray
     * @return the list of {@link Emoji}s
     * @throws IOException if an error occurs while reading the stream or parsing
     *                     the JSONArray
     */
    public static List<Emoji> loadEmojis(InputStream stream) throws IOException {
        JSONArray emojisJSON = new JSONArray(inputStreamToString(stream));
        List<Emoji> emojis = new ArrayList<Emoji>(emojisJSON.length());
        for (int i = 0; i < emojisJSON.length(); i++) {

            JSONObject emojiObject = emojisJSON.getJSONObject(i);

            if (!emojiObject.has("unified")) continue;

            List<String> possibleUnicodes = new LinkedList<String>();
            possibleUnicodes.add(emojiObject.getString("unified"));

            if (emojiObject.has("variations")) {
                JSONArray variations = emojiObject.getJSONArray("variations");
                for (int j = 0; j < variations.length(); j++) {
                    possibleUnicodes.add(variations.getString(j));
                }
            }

            Collections.reverse(possibleUnicodes);

            for (String unicode : possibleUnicodes) {
                emojiObject.put("unified", unicode);
                Emoji emojiVariant = buildEmojiFromJSON(emojiObject);
                if (emojiVariant != null) {
                    emojis.add(emojiVariant);
                }
            }
        }
        return emojis;
    }

    private static String inputStreamToString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String read;
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        br.close();
        return sb.toString();
    }

    protected static Emoji buildEmojiFromJSON(JSONObject json) throws UnsupportedEncodingException {
        if (!json.has("unified")) {
            return null;
        }

        byte[] bytes = extractEmojiFromUnified(json.getString("unified")).getBytes("UTF-8");

        boolean supportsFitzpatrick = json.has("skin_variations");
        List<String> aliases = jsonArrayToStringList(json.getJSONArray("short_names"));

        Category category = Category.parse(json.optString("category"));

        return new Emoji(json.optString("name"), supportsFitzpatrick, aliases, json.has("obsoleted_by"), category, json.optInt("sort_order"), bytes);
    }

    private static List<String> jsonArrayToStringList(JSONArray array) {
        List<String> strings = new ArrayList<String>(array.length());
        for (int i = 0; i < array.length(); i++) {
            strings.add(array.getString(i));
        }
        return strings;
    }

    private static String extractEmojiFromUnified(String unified) {
        String[] unifieds = unified.split("-");
        StringBuilder emoji = new StringBuilder();
        for (int i = 0; i < unifieds.length; i++) {
            int intValue = Integer.parseInt(unifieds[i], 16);
            char[] chars = Character.toChars(intValue);

            emoji.append(new String(chars));
        }

        return emoji.toString();
    }
}
