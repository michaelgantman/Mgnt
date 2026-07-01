package com.mgnt.utils.textutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionComparatorTest {

    private final VersionComparator comparator = new VersionComparator();
    private final static String LOW_VERSION = "1.0";
    private final static String LOW_VERSION_WITH_MORE_SEGMENTS = "1.0.0";
    private final static String HIGH_VERSION = "1.1";

    private final static String LOW_NUMERIC_HIGH_LEXOGRAPHIC_VERSION = "1.9";
    private final static String HIGH_NUMERIC_LOW_LEXOGRAPHIC_VERSION = "1.10";

    @Test
    void compareEqual() throws InvalidVersionFormatException {
        assertEquals(0, comparator.compare(new Version(LOW_VERSION), new Version(LOW_VERSION)));
    }

    @Test
    void compareLessThan() throws InvalidVersionFormatException {
        assertTrue(comparator.compare(new Version(LOW_VERSION), new Version(HIGH_VERSION)) < 0);
    }

    @Test
    void compareGreaterThan() throws InvalidVersionFormatException {
        assertTrue(comparator.compare(new Version(HIGH_VERSION), new Version(LOW_VERSION)) > 0);
    }

    @Test
    void compareNumericalNotLexicographic() throws InvalidVersionFormatException {
        // 1.9 < 1.10 numerically, not 1.10 < 1.9 lexicographically
        assertTrue(comparator.compare(new Version(LOW_NUMERIC_HIGH_LEXOGRAPHIC_VERSION), new Version(HIGH_NUMERIC_LOW_LEXOGRAPHIC_VERSION)) < 0);
        assertTrue(comparator.compare(new Version(HIGH_NUMERIC_LOW_LEXOGRAPHIC_VERSION), new Version(LOW_NUMERIC_HIGH_LEXOGRAPHIC_VERSION)) > 0);
    }

    @Test
    void compareDifferentSegmentCounts() throws InvalidVersionFormatException {
        // "1.0.0" > LOW_VERSION because it has more segments
        assertTrue(comparator.compare(new Version(LOW_VERSION_WITH_MORE_SEGMENTS), new Version(LOW_VERSION)) > 0);
        // "1.0" < "1.0.0"
        assertTrue(comparator.compare(new Version(LOW_VERSION), new Version(LOW_VERSION_WITH_MORE_SEGMENTS)) < 0);
    }

    @Test
    void compareFirstNullThrows() throws InvalidVersionFormatException {
        assertThrows(NullPointerException.class,
                () -> comparator.compare(null, new Version(LOW_VERSION)));
    }

    @Test
    void compareSecondNullThrows() throws InvalidVersionFormatException {
        assertThrows(NullPointerException.class,
                () -> comparator.compare(new Version(LOW_VERSION), null));
    }
}
