package com.mgnt.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    private static final String UTF_8 = "UTF-8";
    private static final String EXPECTED_FILE_NAME = "file.txt";

    @TempDir
    Path tempDir;

    @Test
    void readFileAsStringUtf8() throws IOException {
        Path file = tempDir.resolve("test.txt");
        String content = "Hello, World!\nSecond line";
        Files.write(file, content.getBytes(UTF_8));

        String result = FileUtils.readFileAsString(file.toString(), UTF_8);
        assertEquals(content, result);
    }

    @Test
    void readFileAsStringDefaultCharset() throws IOException {
        Path file = tempDir.resolve("default.txt");
        String content = "Simple ASCII content";
        Files.write(file, content.getBytes(UTF_8));

        String result = FileUtils.readFileAsString(file.toString(), UTF_8);
        assertEquals(content, result);
    }

    @Test
    void readFileAsByteArrayMatchesWrittenBytes() throws IOException {
        Path file = tempDir.resolve("bytes.bin");
        byte[] original = {0x01, 0x02, 0x03, (byte) 0xFF};
        Files.write(file, original);

        byte[] result = FileUtils.readFileAsByteArray(file.toString());
        assertArrayEquals(original, result);
    }

    @Test
    void readFileAsByteArrayEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.bin");
        Files.createFile(file);

        byte[] result = FileUtils.readFileAsByteArray(file.toString());
        assertEquals(0, result.length);
    }

    @Test
    void readFileAsStringFileNotFoundThrowsIOException() {
        assertThrows(IOException.class,
                () -> FileUtils.readFileAsString("/nonexistent/path/file.txt"));
    }

    @Test
    void readFileAsByteArrayFileNotFoundThrowsIOException() {
        assertThrows(IOException.class,
                () -> FileUtils.readFileAsByteArray("/nonexistent/path/file.bin"));
    }

    @Test
    void getFileNameUnixPath() {
        assertEquals(EXPECTED_FILE_NAME, FileUtils.getFileName("/some/path/file.txt"));
    }

    @Test
    void getFileNameWindowsPath() {
        assertEquals(EXPECTED_FILE_NAME, FileUtils.getFileName("C:\\some\\path\\file.txt"));
    }

    @Test
    void getFileNameNoSeparatorReturnsInput() {
        assertEquals(EXPECTED_FILE_NAME, FileUtils.getFileName(EXPECTED_FILE_NAME));
    }

    @Test
    void getFileNameNullReturnsNull() {
        assertNull(FileUtils.getFileName(null));
    }

    @Test
    void getFileNameEmptyReturnsEmpty() {
        assertEquals("", FileUtils.getFileName(""));
    }

    @Test
    void getFileNameTrailingSlash() {
        assertEquals("", FileUtils.getFileName("/some/path/"));
    }
}
