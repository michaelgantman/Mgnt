package com.mgnt.utils.textutils;

import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Gantman
 */
public class VersionComparator implements Comparator<Version> {

    @Override
    public int compare(Version ver1, Version ver2) {
        if (ver1 == null || ver2 == null) {
            throw new NullPointerException("At least one of the compared versions is null");
        }
        if (ver1.equals(ver2)) {
            return 0;
        } else {
            List<Integer> v1 = ver1.getVersionNumbers();
            List<Integer> v2 = ver2.getVersionNumbers();
            for (int i = 0; i < Math.min(v1.size(), v2.size()); i++) {
                if (v1.get(i).equals(v2.get(i))) {
                    continue;
                }
                return v1.get(i).compareTo(v2.get(i));
            }
            return Integer.valueOf(v1.size()).compareTo(Integer.valueOf(v2.size()));
        }
    }
}
