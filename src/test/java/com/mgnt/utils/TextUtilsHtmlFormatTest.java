package com.mgnt.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsHtmlFormatTest {

    @Test
    void formatForHtmlReplacesNewlineWithBrTag() {
        String result = TextUtils.formatStringToPreserveIndentationForHtml("line1\nline2");
        assertTrue(result.contains("<br>"));
    }

    @Test
    void formatForHtmlReplacesSpacesWithNonBreaking() {
        String result = TextUtils.formatStringToPreserveIndentationForHtml("a b");
        assertFalse(result.contains(" "));
        assertTrue(result.contains("\u00A0"));
    }

    @Test
    void formatForHtmlPreservesTextContent() {
        String result = TextUtils.formatStringToPreserveIndentationForHtml("hello");
        assertEquals("hello", result);
    }

    @Test
    void formatForHtmlMultipleSpacesAllReplaced() {
        String input = "  indented";
        String result = TextUtils.formatStringToPreserveIndentationForHtml(input);
        assertFalse(result.contains(" "));
        assertTrue(result.startsWith("\u00A0\u00A0"));
    }

    @Test
    void formatForHtmlNullReturnsNull() {
        assertNull(TextUtils.formatStringToPreserveIndentationForHtml(null));
    }

    @Test
    void formatForHtmlEmptyReturnsEmpty() {
        assertEquals("", TextUtils.formatStringToPreserveIndentationForHtml(""));
    }
}
