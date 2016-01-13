package com.mgnt.utils.textutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Gantman
 */
public class Version implements Comparable<Version> {

    private static final String SEPARATOR = ".";
    private static final String SPLIT_SEPARATOR = "\\.";
    private static final VersionComparator COMPARATOR = new VersionComparator();

    private List<Integer> versionNumbers = new ArrayList<>();

    public Version(String verStr) throws InvalidVersionFormatException {
        if (verStr == null || "".equals(verStr)) {
            throw new InvalidVersionFormatException("Null or empty string may not represent a valid vrsion");
        }
        for (String numStr : verStr.trim().split(SPLIT_SEPARATOR, Integer.MAX_VALUE)) {
            try {
                Integer num = Integer.parseInt(numStr);
                if (num < 0) {
                    throw new InvalidVersionFormatException("Version may not contain a negative number");
                }
                versionNumbers.add(num);

            } catch (NumberFormatException nfe) {
                throw new InvalidVersionFormatException("Version must contain valid numeric values separated by '" + SEPARATOR + "'", nfe);
            }
        }
        if (versionNumbers.isEmpty()) {
            /*
			 * This is not expected to ever occur, but just in case...
			 */
            throw new InvalidVersionFormatException("Blank version error");
        }
    }

    public List<Integer> getVersionNumbers() {
        return Collections.unmodifiableList(versionNumbers);
    }

    @Override
    public int compareTo(Version otherVersion) {
        if (otherVersion == null) {
            throw new NullPointerException("Version passed for comparison is null");
        }
        return COMPARATOR.compare(this, otherVersion);
    }

    @Override
    public String toString() {
        StringBuilder ver = new StringBuilder();
        for (Integer i : versionNumbers) {
            ver.append(i).append(SEPARATOR);
        }
        ver.deleteCharAt(ver.length() - 1);
        return ver.toString();
    }

    @Override
    public int hashCode() {
        return versionNumbers.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Version other = (Version) obj;
        return versionNumbers.equals(other.versionNumbers);
    }
}
