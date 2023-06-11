package com.msp31.storage1c.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListTrimmer {
    public static List<String> trim(List<String> strings) {
        if (strings == null)
            return null;

        return strings.stream().map(String::trim).toList();
    }

    public static Set<String> trimToSet(List<String> strings) {
        if (strings == null)
            return null;

        return strings.stream().map(String::trim).collect(Collectors.toSet());
    }
}
