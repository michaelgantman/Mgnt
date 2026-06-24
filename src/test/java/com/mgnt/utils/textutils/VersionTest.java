package com.mgnt.utils.textutils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    void parseThreeSegments() throws InvalidVersionFormatException {
        Version v = new Version("1.2.3");
        assertEquals(Arrays.asList(1, 2, 3), v.getVersionNumbers());
    }

    @Test
    void parseSingleSegment() throws InvalidVersionFormatException {
        Version v = new Version("42");
        assertEquals(Arrays.asList(42), v.getVersionNumbers());
    }

    @Test
    void parseLeadingTrailingWhitespaceIgnored() throws InvalidVersionFormatException {
        Version v = new Version("  7.34.17  ");
        assertEquals(Arrays.asList(7, 34, 17), v.getVersionNumbers());
    }

    @Test
    void parseZeroValid() throws InvalidVersionFormatException {
        Version v = new Version("0");
        assertEquals(Arrays.asList(0), v.getVersionNumbers());
    }

    @Test
    void parseNullThrows() {
        assertThrows(InvalidVersionFormatException.class, () -> new Version(null));
    }

    @Test
    void parseEmptyThrows() {
        assertThrows(InvalidVersionFormatException.class, () -> new Version(""));
    }

    @Test
    void parseNegativeSegmentThrows() {
        assertThrows(InvalidVersionFormatException.class, () -> new Version("-1.0"));
    }

    @Test
    void parseNonNumericSegmentThrows() {
        assertThrows(InvalidVersionFormatException.class, () -> new Version("1.a.3"));
    }

    @Test
    void compareToLesser() throws InvalidVersionFormatException {
        assertTrue(new Version("1.9").compareTo(new Version("1.10")) < 0);
    }

    @Test
    void compareToEqual() throws InvalidVersionFormatException {
        assertEquals(0, new Version("2.0").compareTo(new Version("2.0")));
    }

    @Test
    void compareToGreater() throws InvalidVersionFormatException {
        assertTrue(new Version("2.1").compareTo(new Version("2.0")) > 0);
    }

    @Test
    void compareToDifferentSegmentCountsMoreMeansHigher() throws InvalidVersionFormatException {
        assertTrue(new Version("1.0.0").compareTo(new Version("1.0")) > 0);
    }

    @Test
    void compareToNullThrows() throws InvalidVersionFormatException {
        assertThrows(NullPointerException.class, () -> new Version("1.0").compareTo(null));
    }

    @Test
    void toStringRoundTrip() throws InvalidVersionFormatException {
        assertEquals("1.2.3", new Version("1.2.3").toString());
    }

    @Test
    void toStringSingleSegment() throws InvalidVersionFormatException {
        assertEquals("5", new Version("5").toString());
    }

    @Test
    void equalsSameVersions() throws InvalidVersionFormatException {
        assertEquals(new Version("1.0"), new Version("1.0"));
    }

    @Test
    void equalsDifferentVersions() throws InvalidVersionFormatException {
        assertNotEquals(new Version("1.0"), new Version("1.1"));
    }

    @Test
    void equalsNullReturnsFalse() throws InvalidVersionFormatException {
        assertNotEquals(new Version("1.0"), null);
    }

    @Test
    void equalsDifferentTypeReturnsFalse() throws InvalidVersionFormatException {
        assertNotEquals(new Version("1.0"), "1.0");
    }

    @Test
    void hashCodeEqualVersionsSameHashCode() throws InvalidVersionFormatException {
        assertEquals(new Version("1.0").hashCode(), new Version("1.0").hashCode());
    }

    @Test
    void getVersionNumbersReturnsUnmodifiableList() throws InvalidVersionFormatException {
        Version v = new Version("1.2.3");
        assertThrows(UnsupportedOperationException.class,
                () -> v.getVersionNumbers().add(4));
    }
}
