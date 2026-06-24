package com.mgnt.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsNumericParsingTest {

    // --- int ---

    @Test
    void parseIntValidString() {
        assertEquals(42, TextUtils.parseStringToInt("42", 0));
    }

    @Test
    void parseIntInvalidStringReturnsDefault() {
        assertEquals(0, TextUtils.parseStringToInt("36d", 0));
    }

    @Test
    void parseIntNullReturnsDefault() {
        assertEquals(-1, TextUtils.parseStringToInt(null, -1));
    }

    @Test
    void parseIntEmptyStringReturnsDefault() {
        assertEquals(-1, TextUtils.parseStringToInt("", -1));
    }

    @Test
    void parseIntNegativeValue() {
        assertEquals(-7, TextUtils.parseStringToInt("-7", 0));
    }

    @Test
    void parseIntWithErrorMessagesValidString() {
        assertEquals(100, TextUtils.parseStringToInt("100", 0, "null error", "format error"));
    }

    @Test
    void parseIntWithErrorMessagesInvalidStringReturnsDefault() {
        assertEquals(99, TextUtils.parseStringToInt("bad", 99, "null error", "format error"));
    }

    // --- long ---

    @Test
    void parseLongValid() {
        assertEquals(9_000_000_000L, TextUtils.parseStringToLong("9000000000", 0L));
    }

    @Test
    void parseLongInvalidReturnsDefault() {
        assertEquals(99L, TextUtils.parseStringToLong("xyz", 99L));
    }

    @Test
    void parseLongNullReturnsDefault() {
        assertEquals(-1L, TextUtils.parseStringToLong(null, -1L));
    }

    // --- double ---

    @Test
    void parseDoubleValid() {
        assertEquals(3.14, TextUtils.parseStringToDouble("3.14", 0.0), 0.0001);
    }

    @Test
    void parseDoubleInvalidReturnsDefault() {
        assertEquals(1.0, TextUtils.parseStringToDouble("bad", 1.0), 0.0001);
    }

    @Test
    void parseDoubleNullReturnsDefault() {
        assertEquals(0.0, TextUtils.parseStringToDouble(null, 0.0), 0.0001);
    }

    // --- float ---

    @Test
    void parseFloatValid() {
        assertEquals(2.5f, TextUtils.parseStringToFloat("2.5", 0.0f), 0.0001f);
    }

    @Test
    void parseFloatInvalidReturnsDefault() {
        assertEquals(1.0f, TextUtils.parseStringToFloat("bad", 1.0f), 0.0001f);
    }

    // --- byte ---

    @Test
    void parseByteValid() {
        assertEquals((byte) 127, TextUtils.parseStringToByte("127", (byte) 0));
    }

    @Test
    void parseByteOverflowReturnsDefault() {
        assertEquals((byte) 0, TextUtils.parseStringToByte("200", (byte) 0));
    }

    @Test
    void parseByteNullReturnsDefault() {
        assertEquals((byte) 5, TextUtils.parseStringToByte(null, (byte) 5));
    }

    // --- short ---

    @Test
    void parseShortValid() {
        assertEquals((short) 1000, TextUtils.parseStringToShort("1000", (short) 0));
    }

    @Test
    void parseShortInvalidReturnsDefault() {
        assertEquals((short) -1, TextUtils.parseStringToShort("xyz", (short) -1));
    }

    // --- BigDecimal ---

    @Test
    void parseBigDecimalValid() {
        assertEquals(new BigDecimal("123.456"),
                TextUtils.parseStringToBigDecimal("123.456", BigDecimal.ZERO));
    }

    @Test
    void parseBigDecimalInvalidReturnsDefault() {
        assertEquals(BigDecimal.ZERO,
                TextUtils.parseStringToBigDecimal("nope", BigDecimal.ZERO));
    }

    @Test
    void parseBigDecimalNullReturnsDefault() {
        assertEquals(BigDecimal.TEN,
                TextUtils.parseStringToBigDecimal(null, BigDecimal.TEN));
    }

    // --- BigInteger ---

    @Test
    void parseBigIntegerValid() {
        assertEquals(new BigInteger("99999999999999"),
                TextUtils.parseStringToBigInteger("99999999999999", BigInteger.ZERO));
    }

    @Test
    void parseBigIntegerInvalidReturnsDefault() {
        assertEquals(BigInteger.ONE,
                TextUtils.parseStringToBigInteger("bad", BigInteger.ONE));
    }

    @Test
    void parseBigIntegerNullReturnsDefault() {
        assertEquals(BigInteger.ZERO,
                TextUtils.parseStringToBigInteger(null, BigInteger.ZERO));
    }
}
