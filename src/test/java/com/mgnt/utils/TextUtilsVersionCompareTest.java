package com.mgnt.utils;

import com.mgnt.utils.textutils.InvalidVersionFormatException;
import com.mgnt.utils.textutils.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsVersionCompareTest {

    @Test
    void compareVersionStringsEqual() throws InvalidVersionFormatException {
        assertEquals(0, TextUtils.compareVersions("1.0", "1.0"));
    }

    @Test
    void compareVersionStringsLessThan() throws InvalidVersionFormatException {
        assertEquals(-1, TextUtils.compareVersions("1.0", "2.0"));
    }

    @Test
    void compareVersionStringsGreaterThan() throws InvalidVersionFormatException {
        assertEquals(1, TextUtils.compareVersions("2.0", "1.0"));
    }

    @Test
    void compareVersionObjectsEqual() throws InvalidVersionFormatException {
        assertEquals(0, TextUtils.compareVersions(new Version("1.5"), new Version("1.5")));
    }

    @Test
    void compareVersionObjectsNumericalOrder() throws InvalidVersionFormatException {
        assertTrue(TextUtils.compareVersions(new Version("1.5"), new Version("1.10")) < 0);
    }

    @Test
    void compareVersionObjectAndStringGreater() throws InvalidVersionFormatException {
        assertEquals(1, TextUtils.compareVersions(new Version("3.0"), "2.9"));
    }

    @Test
    void compareVersionStringAndObjectLesser() throws InvalidVersionFormatException {
        assertEquals(-1, TextUtils.compareVersions("1.0", new Version("1.1")));
    }

    @Test
    void compareVersionsInvalidStringThrows() {
        assertThrows(InvalidVersionFormatException.class,
                () -> TextUtils.compareVersions("1.abc", "1.0"));
    }

    @Test
    void compareVersionsMultiSegment() throws InvalidVersionFormatException {
        assertTrue(TextUtils.compareVersions("1.2.3", "1.2.4") < 0);
        assertTrue(TextUtils.compareVersions("2.0.0", "1.9.9") > 0);
        assertEquals(0, TextUtils.compareVersions("1.0.0", "1.0.0"));
    }
}
