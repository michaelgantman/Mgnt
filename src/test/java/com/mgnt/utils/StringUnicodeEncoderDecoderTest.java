package com.mgnt.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUnicodeEncoderDecoderTest {

    @Test
    void encodeKnownAscii() {
        // H=0048, e=0065, l=006c, l=006c, o=006f
        String encoded = StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence("Hello");
        assertEquals("\\u0048\\u0065\\u006c\\u006c\\u006f", encoded);
    }

    @Test
    void decodeKnownAscii() {
        String decoded = StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                "\\u0048\\u0065\\u006c\\u006c\\u006f");
        assertEquals("Hello", decoded);
    }

    @Test
    void roundTripAscii() {
        String original = "Hello, World!";
        assertEquals(original,
                StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                        StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(original)));
    }

    @Test
    void roundTripHebrew() {
        String hebrew = "\u05E9\u05DC\u05D5\u05DD"; // שלום
        assertEquals(hebrew,
                StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                        StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(hebrew)));
    }

    @Test
    void roundTripChineseCharacters() {
        String chinese = "\u4E2D\u6587"; // 中文
        assertEquals(chinese,
                StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                        StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(chinese)));
    }

    @Test
    void roundTripJapanese() {
        String japanese = "\u3053\u3093\u306B\u3061\u306F"; // こんにちは
        assertEquals(japanese,
                StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                        StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(japanese)));
    }

    @Test
    void roundTripMixedAsciiAndUnicode() {
        String mixed = "Hello \u05E9\u05DC\u05D5\u05DD World";
        assertEquals(mixed,
                StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                        StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(mixed)));
    }

    @Test
    void encodeNullReturnsEmpty() {
        assertEquals("", StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(null));
    }

    @Test
    void encodeEmptyReturnsEmpty() {
        assertEquals("", StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(""));
    }

    @Test
    void decodeUpperCaseUPrefixAccepted() {
        String decoded = StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                "\\U0048\\U0065\\U006c\\U006c\\U006f");
        assertEquals("Hello", decoded);
    }

    @Test
    void decodeWithWhitespaceBetweenCodesAccepted() {
        String decoded = StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString(
                "\\u0048 \\u0065 \\u006c \\u006c \\u006f");
        assertEquals("Hello", decoded);
    }

    @Test
    void decodeInvalidInputThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString("not-unicode"));
    }
}
