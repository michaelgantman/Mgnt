package com.mgnt.utils.textutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionRangeTest {

    private VersionRange range; // [1.0 - 3.0]

    @BeforeEach
    void setUp() throws Exception {
        range = new VersionRange("1.0", "3.0");
    }

    @Test
    void isInRangeLowerBoundIncluded() throws InvalidVersionFormatException {
        assertTrue(range.isInRange("1.0"));
    }

    @Test
    void isInRangeUpperBoundIncluded() throws InvalidVersionFormatException {
        assertTrue(range.isInRange("3.0"));
    }

    @Test
    void isInRangeMidpoint() throws InvalidVersionFormatException {
        assertTrue(range.isInRange("2.5"));
    }

    @Test
    void isInRangeBelowRange() throws InvalidVersionFormatException {
        assertFalse(range.isInRange("0.9"));
    }

    @Test
    void isInRangeAboveRange() throws InvalidVersionFormatException {
        assertFalse(range.isInRange("3.1"));
    }

    @Test
    void isInRangeNullReturnsFalse() {
        assertFalse(range.isInRange((Version) null));
    }

    @Test
    void isAboveRangeTrueWhenAbove() throws InvalidVersionFormatException {
        assertTrue(range.isAboveRange("3.1"));
    }

    @Test
    void isAboveRangeFalseWhenInRange() throws InvalidVersionFormatException {
        assertFalse(range.isAboveRange("2.0"));
    }

    @Test
    void isAboveRangeFalseAtUpperBound() throws InvalidVersionFormatException {
        assertFalse(range.isAboveRange("3.0"));
    }

    @Test
    void isBelowRangeTrueWhenBelow() throws InvalidVersionFormatException {
        assertTrue(range.isBellowRange(new Version("0.9")));
    }

    @Test
    void isBelowRangeFalseWhenInRange() throws InvalidVersionFormatException {
        assertFalse(range.isBellowRange(new Version("2.0")));
    }

    @Test
    void isBelowRangeFalseAtLowerBound() throws InvalidVersionFormatException {
        assertFalse(range.isBellowRange(new Version("1.0")));
    }

    @Test
    void isBelowRangeStringOverload() throws InvalidVersionFormatException {
        assertTrue(range.isBelowRange("0.5"));
        assertFalse(range.isBelowRange("2.0"));
    }

    @Test
    void isOverlapOverlapping() throws Exception {
        VersionRange other = new VersionRange("2.0", "4.0");
        assertTrue(range.isOverlap(other));
    }

    @Test
    void isOverlapTouchingAtUpperBound() throws Exception {
        VersionRange touching = new VersionRange("3.0", "5.0");
        assertTrue(range.isOverlap(touching));
    }

    @Test
    void isOverlapNoOverlap() throws Exception {
        VersionRange noOverlap = new VersionRange("4.0", "5.0");
        assertFalse(range.isOverlap(noOverlap));
    }

    @Test
    void isOverlapNullReturnsFalse() {
        assertFalse(range.isOverlap(null));
    }

    @Test
    void isOverlapContainedRange() throws Exception {
        VersionRange inner = new VersionRange("1.5", "2.5");
        assertTrue(range.isOverlap(inner));
    }

    @Test
    void constructorInvalidRangeThrows() {
        assertThrows(InvalidVersionRangeException.class,
                () -> new VersionRange("3.0", "1.0"));
    }

    @Test
    void constructorEqualBoundsValid() throws Exception {
        VersionRange point = new VersionRange("2.0", "2.0");
        assertTrue(point.isInRange("2.0"));
        assertFalse(point.isInRange("2.1"));
    }

    @Test
    void constructorSingleVersionStringPointRange() throws Exception {
        VersionRange point = new VersionRange("2.0");
        assertTrue(point.isInRange("2.0"));
        assertFalse(point.isInRange("2.1"));
    }

    @Test
    void constructorFromHyphenDelimitedString() throws Exception {
        VersionRange r = new VersionRange("1.0-3.0");
        assertTrue(r.isInRange("2.0"));
        assertFalse(r.isInRange("0.9"));
    }

    @Test
    void constructorNullStringThrows() {
        assertThrows(InvalidVersionRangeException.class,
                () -> new VersionRange((String) null));
    }

    @Test
    void getFromAndToVersion() throws Exception {
        assertEquals("1.0", range.getFromVersion().toString());
        assertEquals("3.0", range.getToVersion().toString());
    }

    @Test
    void toStringContainsBothBounds() {
        String s = range.toString();
        assertTrue(s.contains("1.0"));
        assertTrue(s.contains("3.0"));
        assertTrue(s.contains("-"));
    }
}
