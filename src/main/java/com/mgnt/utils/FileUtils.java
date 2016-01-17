package com.mgnt.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;

/**
 * This class provides various File I/O Services. In general the ability to read and write files as Strings or as byte arrays
 *
 * @author Michael Gantman
 */
public class FileUtils {

    private static final int MAX_READ_FAILURES = 10;

    /**
     * This method reads the file as String and assumes that file contains information in specified encoding. It can read files that are no larger
     * than 2147483647 bytes
     *
     * @param path        String that contains the path to the file to be read
     * @param charsetName String that contains the encoding name
     * @return String that contains the read content of the file
     * @throws IOException if any problem occurred
     */
    public static String readFileAsString(String path, String charsetName) throws IOException {
        ByteBuffer content = internalReadFileAsByteArray(path);
        CharBuffer result = Charset.forName(charsetName).decode(content);
        char[] charContent = null;
        if (result.hasArray()) {
            charContent = result.array();
        } else {
            charContent = new char[result.limit()];
            result.get(charContent);
        }
        return new String(charContent);

    }

    /**
     * This method reads the file as String. The default character set is used. It can read files that are no larger than 2147483647 bytes
     *
     * @param path String that contains the path to the file to be read
     * @return String that contains the read content of the file
     * @throws IOException if any problem occurred
     */
    public static String readFileAsString(String path) throws IOException {
        return readFileAsString(path, Charset.defaultCharset().name());
    }

    /**
     * This method reads file as binary data. It can read files that are no larger than 2147483647 bytes
     *
     * @param path String that contains the path to the file to be read
     * @return byte array that contains the file content
     * @throws IOException if any problem occurred
     */
    public static byte[] readFileAsByteArray(String path) throws IOException {
        return internalReadFileAsByteArray(path).array();
    }

    /**
     * This method that actually performs binary file content reading. It can read files that are no larger than 2147483647 bytes
     *
     * @param path String that contains the path to the file to be read
     * @return byte array that contains the file content
     * @throws IOException if any problem occurred
     */
    private static ByteBuffer internalReadFileAsByteArray(String path) throws IOException {
        ByteBuffer byteBuffer = null;
        Path filePath = FileSystems.getDefault().getPath(path);
        try (
                FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ);) {
            Long size = fileChannel.size();
            if (size > Integer.MAX_VALUE) {
                throw new IOException(MessageFormat.format(
                        "File {0} is too large. Its size is {1,number,integer} bytes which is larger " +
                                "then this method could handle ( {2,number,integer})", path, size, Integer.MAX_VALUE));
            }
            byteBuffer = ByteBuffer.allocate(size.intValue());
            int readBytes = 0;
            int totalReadBytes = 0;
            int failureCounter = 0;
            while ((readBytes = fileChannel.read(byteBuffer)) >= 0 && totalReadBytes < size.intValue()) {
                if (readBytes > 0) {
                    totalReadBytes += readBytes;
                    if (failureCounter > 0) {
                        failureCounter = 0;
                    }
                } else {
                    if (++failureCounter >= MAX_READ_FAILURES) {
                        throw new IOException(MessageFormat.format("File {0} could not be read for unknown reason", path));
                    }
                }
            }
        }
        return (ByteBuffer) byteBuffer.flip();
    }

    static public String getFileName(String fullPath){
        if(StringUtils.isNotEmpty(fullPath)) {
            int lastFileSeparator1 = fullPath.lastIndexOf("\\");
            int lastFileSeparator2 = fullPath.lastIndexOf("/");
            if(lastFileSeparator1>0|| lastFileSeparator2>0) {
                return fullPath.substring(Math.max(lastFileSeparator1, lastFileSeparator2) + 1);
            }
        }
        return fullPath;
    }
}
