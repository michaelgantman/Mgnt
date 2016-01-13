package com.mgnt.utils.textutils;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * This class represents a Version Range. It has the lower and upper limits and allows to check if a version is within range or not and if two ranges
 * overlap or not
 *
 * @author Michael Gantman
 */
public class VersionRange implements Serializable {

    private static final long serialVersionUID = 236297683414697942L;

    private static final String VERSION_DELIMITER = "-";

    private Version _fromVersion;
    private Version _toVersion;

    /**
     * This constructor constructs the Range with its lower and upper limits
     *
     * @param fromVersion Version that contains lower limit of the range
     * @param toVersion   Version that contains upper limit of the range
     * @throws InvalidVersionRangeException if lower limit of the range is greater than upper limit
     */
    public VersionRange(Version fromVersion, Version toVersion) throws InvalidVersionRangeException {
        _fromVersion = fromVersion;
        _toVersion = toVersion;
        validateData();
    }

    /**
     * This constructor constructs the Range with its lower and upper limits. It first converts String parameters into Version values
     *
     * @param fromVersion String that contains lower limit of the range
     * @param toVersion   String that contains upper limit of the range
     * @throws InvalidVersionRangeException  if lower limit of the range is greater than upper limit
     * @throws InvalidVersionFormatException if one of the Strings is not a valid version
     */
    public VersionRange(String fromVersion, String toVersion)
            throws InvalidVersionRangeException, InvalidVersionFormatException {
        this(new Version(fromVersion.trim()), new Version(toVersion.trim()));
    }

    /**
     * This constructor constructs the Range with its lower and upper limits. It first converts String parameter into Version value
     *
     * @param fromVersion String that contains lower limit of the range
     * @param toVersion   String that contains upper limit of the range
     * @throws InvalidVersionRangeException  if lower limit of the range is greater than upper limit
     * @throws InvalidVersionFormatException if String parameter is not a valid version
     */
    public VersionRange(Version fromVersion, String toVersion)
            throws InvalidVersionRangeException, InvalidVersionFormatException {
        this(fromVersion, new Version(toVersion.trim()));
    }

    /**
     * This constructor constructs the Range with its lower and upper limits. It first converts String parameter into Version value
     *
     * @param fromVersion String that contains lower limit of the range
     * @param toVersion   String that contains upper limit of the range
     * @throws InvalidVersionRangeException  if lower limit of the range is greater than upper limit
     * @throws InvalidVersionFormatException if String parameter is not a valid version
     */
    public VersionRange(String fromVersion, Version toVersion)
            throws InvalidVersionRangeException, InvalidVersionFormatException {
        this(new Version(fromVersion.trim()), toVersion);
    }

    /**
     * This constructor creates the Range with the same lower and upper limits
     *
     * @param ver String that holds a single version
     * @throws InvalidVersionRangeException if parameter is null
     */
    public VersionRange(Version ver) throws InvalidVersionRangeException {
        this(ver, ver);
    }

    /**
     * This constructor assumes that the string is a version range in format "[Version String] - [Version String]" It splits the string by the "-"
     * delimiter and attempts to create the range the same way as constructor {@link #VersionRange(String, String)} would. If the original String
     * contains only one version without a delimiter (i.e. "[Version String]") the attempt is made to convert this String into single Version object
     * and than to create VersionRange with the same lower and upper limits
     *
     * @param versionRange String in format "[Version String] - [Version String]" or "[Version String]"
     * @throws InvalidVersionRangeException  if lower limit of the range is greater than upper limit
     * @throws InvalidVersionFormatException if String parameter is not a valid version
     */
    public VersionRange(String versionRange) throws InvalidVersionRangeException, InvalidVersionFormatException {
        if (versionRange == null || "".equals(versionRange)) {
            throw new InvalidVersionRangeException("NUll or blank argument");
        }
        String[] versions = versionRange.split(VERSION_DELIMITER, 2);
        if (versions.length == 1) {
            _fromVersion = _toVersion = new Version(versions[0].trim());
        } else {
            _fromVersion = new Version(versions[0].trim());
            _toVersion = new Version(versions[1].trim());
        }
        validateData();
    }

    /**
     * @return Version which is lower range limit
     */
    public Version getFromVersion() {
        return _fromVersion;
    }

    /**
     * @return Version which is upper range limit
     */
    public Version getToVersion() {
        return _toVersion;
    }

    /**
     * This method checks if the version falls within range. Null version is always out of range and the comparison is all inclusive
     *
     * @param ver Version to be checked
     * @return true if Version falls within range and false otherwise
     */
    public boolean isInRange(Version ver) {
        boolean result = false;
        if (ver != null) {
            if (_fromVersion.compareTo(ver) <= 0 && _toVersion.compareTo(ver) >= 0) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This method first converts String parameter into Version and checks if the version falls within range. Null version is always out of range and
     * the comparison is all inclusive
     *
     * @param ver String that contains Version value to be checked
     * @return true if Version falls within range and false otherwise
     * @throws InvalidVersionFormatException if String is not a valid version value
     */
    public boolean isInRange(String ver) throws InvalidVersionFormatException {
        return isInRange(new Version(ver));
    }

    /**
     * Checks if the ver is above the range
     *
     * @param ver Version to be checked
     * @return true if version is above range false otherwise
     */
    public boolean isAboveRange(Version ver) {
        boolean result = false;
        if (ver.compareTo(getToVersion()) > 0) {
            result = true;
        }
        return result;
    }

    /**
     * Converts the String into Version and than checks if the ver is above the range
     *
     * @param ver String to be checked
     * @return true if version is above range false otherwise
     * @throws InvalidVersionFormatException
     */
    public boolean isAboveRange(String ver) throws InvalidVersionFormatException {
        return isAboveRange(new Version(ver));
    }

    /**
     * Checks if the ver is below the range
     *
     * @param ver Version to be checked
     * @return true if version is below range false otherwise
     */
    public boolean isBellowRange(Version ver) {
        boolean result = false;
        if (ver.compareTo(getFromVersion()) < 0) {
            result = true;
        }
        return result;
    }

    /**
     * Converts the String into Version and than checks if the ver is below the range
     *
     * @param ver String to be checked
     * @return true if version is bellow range false otherwise
     * @throws InvalidVersionFormatException
     */
    public boolean isBelowRange(String ver) throws InvalidVersionFormatException {
        return isBellowRange(new Version(ver));
    }

    public boolean isOverlap(VersionRange otherRange) {
        boolean result = false;
        if (otherRange != null) {
            if (isInRange(otherRange.getFromVersion()) || isInRange(otherRange.getToVersion())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {2} {1}", _fromVersion.toString(), _toVersion.toString(), VERSION_DELIMITER);
    }

    private void validateData()
            throws InvalidVersionRangeException {
        if (_fromVersion == null || _toVersion == null) {
            throw new InvalidVersionRangeException("One of the range limits is null");
        }
        if (_fromVersion.compareTo(_toVersion) > 0) {
            throw new InvalidVersionRangeException("Lower range limit is greater then upper range limit");
        }
    }
}
