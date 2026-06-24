package com.mgnt.utils.textutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionComparatorTest {

    private final VersionComparator comparator = new VersionComparator();

    @Test
    void compareEqual() throws InvalidVersionFormatException {
        assertEquals(0, comparator.compare(new Version("1.0"), new Version("1.0")));
    }

    @Test
    void compareLessThan() throws InvalidVersionFormatException {
        assertTrue(comparator.compare(new Version("1.0"), new Version("1.1")) < 0);
    }

    @Test
    void compareGreaterThan() throws InvalidVersionFormatException {
        assertTrue(comparator.compare(new Version("2.0"), new Version("1.9")) > 0);
    }

    @Test
    void compareNumericalNotLexicographic() throws InvalidVersionFormatException {
        // 1.9 < 1.10 numerically, not 1.10 < 1.9 lexicographically
        assertTrue(comparator.compare(new Version("1.9"), new Version("1.10")) < 0);
    }

    @Test
    void compareDifferentSegmentCounts() throws InvalidVersionFormatException {
        // "1.0.0" > "1.0" because it has more segments
        assertTrue(comparator.compare(new Version("1.0.0"), new Version("1.0")) > 0);
        // "1.0" < "1.0.0"
        assertTrue(comparator.compare(new Version("1.0"), new Version("1.0.0")) < 0);
    }

    @Test
    void compareFirstNullThrows() throws InvalidVersionFormatException {
        assertThrows(NullPointerException.class,
                () -> comparator.compare(null, new Version("1.0")));
    }

    @Test
    void compareSecondNullThrows() throws InvalidVersionFormatException {
        assertThrows(NullPointerException.class,
                () -> comparator.compare(new Version("1.0"), null));
    }
}
