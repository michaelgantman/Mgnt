package com.mgnt.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsStacktraceTest {

    private static final String TEST_PACKAGE = "com.example.";

    private String[] savedPackages;

    @BeforeEach
    void saveRelevantPackages() {
        savedPackages = TextUtils.getRelevantPackage();
    }

    @AfterEach
    void restoreRelevantPackages() {
        TextUtils.setRelevantPackage(savedPackages);
    }

    @Test
    void getStacktraceFullModeContainsExceptionMessage() {
        RuntimeException e = new RuntimeException("test-error-message");
        String trace = TextUtils.getStacktrace(e, false);
        assertTrue(trace.contains("test-error-message"));
        assertTrue(trace.contains("RuntimeException"));
    }

    @Test
    void getStacktraceWithExplicitPackageFiltersIrrelevantLines() {
        RuntimeException e = new RuntimeException("filtered");
        String filtered = TextUtils.getStacktrace(e, true, "com.mgnt.");
        assertNotNull(filtered);
        assertTrue(filtered.contains("filtered"));
    }

    @Test
    void getStacktraceFromCharSequenceFiltersIrrelevantLines() {
        // Synthetic stacktrace: line 3 is relevant, line 4 is first irrelevant after relevant
        // block (kept), line 5 is second irrelevant (replaced by "...")
        String stacktrace =
                "java.lang.RuntimeException: some error\n" +
                "\tat java.lang.Thread.run(Thread.java:748)\n" +
                "\tat com.example.Foo.doIt(Foo.java:42)\n" +
                "\tat java.util.concurrent.Future.get(Future.java:100)\n" +
                "\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n";

        String result = TextUtils.getStacktrace(stacktrace, TEST_PACKAGE);

        assertTrue(result.contains("some error"));
        assertTrue(result.contains("com.example.Foo.doIt"));
        assertTrue(result.contains("java.util.concurrent.Future.get"));
        assertFalse(result.contains("Executors$RunnableAdapter"));
        assertTrue(result.contains("\t..."));
    }

    @Test
    void getStacktraceFromCharSequenceWithGlobalPackageSet() {
        TextUtils.setRelevantPackage(TEST_PACKAGE);
        String stacktrace =
                "java.lang.RuntimeException: global test\n" +
                "\tat com.example.Bar.run(Bar.java:10)\n" +
                "\tat java.lang.Thread.run(Thread.java:748)\n" +
                "\tat java.lang.Object.wait(Object.java:100)\n";

        String result = TextUtils.getStacktrace(stacktrace);
        assertTrue(result.contains("global test"));
        assertTrue(result.contains("com.example.Bar.run"));
    }

    @Test
    void getStacktraceNoRelevantPackageSetReturnsFull() {
        TextUtils.setRelevantPackage((String[]) null);
        RuntimeException e = new RuntimeException("full");
        String trace = TextUtils.getStacktrace(e, true);
        assertTrue(trace.contains("full"));
    }

    @Test
    void setAndGetRelevantPackageRoundTrip() {
        TextUtils.setRelevantPackage("com.foo.", "com.bar.");
        String[] packages = TextUtils.getRelevantPackage();
        assertArrayEquals(new String[]{"com.foo.", "com.bar."}, packages);
    }

    @Test
    void getStacktraceCausedByContainsBothMessages() {
        RuntimeException cause = new RuntimeException("root cause");
        RuntimeException wrapper = new RuntimeException("wrapper error", cause);
        String trace = TextUtils.getStacktrace(wrapper, false);
        assertTrue(trace.contains("wrapper error"));
        assertTrue(trace.contains("root cause"));
        assertTrue(trace.contains("Caused by:"));
    }
}
