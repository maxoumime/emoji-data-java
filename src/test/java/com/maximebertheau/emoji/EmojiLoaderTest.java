package com.maximebertheau.emoji;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class EmojiLoaderTest {
    @Test
    public void load_empty_database_returns_empty_list() throws IOException {
        // GIVEN
        byte[] bytes = new JSONArray().toString().getBytes("UTF-8");
        InputStream stream = new ByteArrayInputStream(bytes);

        // WHEN
        List<Emoji> emojis = EmojiLoader.loadEmojis(stream);

        // THEN
        assertEquals(0, emojis.size());
    }

    @Test
    public void buildEmojiFromJSON() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"SMILING FACE WITH OPEN MOUTH AND SMILING EYES\"," +
                "    \"unified\": \"1F604\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smile\"" +
                "    ]," +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
        assertEquals("😄", emoji.getUnicode());
        assertEquals(1, emoji.getAliases().size());
        assertEquals("smile", emoji.getAliases().get(0));
    }

    @Test
    public void buildEmojiFromJSON_without_description_sets_a_null_description()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"SMILING FACE WITH OPEN MOUTH AND SMILING EYES\"," +
                "    \"unified\": \"1F604\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smile\"" +
                "    ]," +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
    }

    @Test
    public void buildEmojiFromJSON_without_unicode_returns_null()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{"
                + "\"aliases\": [\"smile\"],"
                + "\"tags\": [\"happy\", \"joy\", \"pleased\"]"
                + "}");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNull(emoji);
    }

    @Test
    public void buildEmojiFromJSON_computes_the_html_codes()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"SMILING FACE WITH OPEN MOUTH AND SMILING EYES\"," +
                "    \"unified\": \"1F604\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smile\"" +
                "    ]," +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
        assertEquals("😄", emoji.getUnicode());
        assertEquals("&#128516;", emoji.getHtmlDecimal());
        assertEquals("&#x1f604;", emoji.getHtmlHexadecimal());
    }

    @Test
    public void buildEmojiFromJSON_with_support_for_fitzpatrick_true()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"HAPPY PERSON RAISING ONE HAND\"," +
                "    \"unified\": \"1F64B\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smile\"" +
                "    ]," +
                "    \"skin_variations\": {" +
                "      \"1F64B-1F3FB\": {}," +
                "      \"1F64B-1F3FC\": {}," +
                "      \"1F64B-1F3FD\": {}," +
                "      \"1F64B-1F3FE\": {}," +
                "      \"1F64B-1F3FF\": {}" +
                "    }" +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
        assertTrue(emoji.supportsFitzpatrick());
    }

    @Test
    public void buildEmojiFromJSON_with_support_for_fitzpatrick_false()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"SMILING FACE WITH OPEN MOUTH AND SMILING EYES\"," +
                "    \"unified\": \"1F604\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smile\"" +
                "    ]," +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
        assertFalse(emoji.supportsFitzpatrick());
    }

    @Test
    public void buildEmojiFromJSON_without_support_for_fitzpatrick()
            throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject("{" +
                "    \"name\": \"SMILING FACE WITH OPEN MOUTH\"," +
                "    \"unified\": \"1F603\"," +
                "    \"variations\": [" +
                "      " +
                "    ]," +
                "    \"short_names\": [" +
                "      \"smiley\"" +
                "    ]," +
                "  }");

        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

        // THEN
        assertNotNull(emoji);
        assertFalse(emoji.supportsFitzpatrick());
    }
}
