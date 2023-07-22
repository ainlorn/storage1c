package com.msp31.storage1c.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagUtils {
    public static List<String> normalizeForSearch(Set<String> tags) {
        // collect to set to ensure no duplicates
        var tagsUpper = tags.stream().map(String::toUpperCase).collect(Collectors.toSet());
        return new ArrayList<>(tagsUpper);
    }
}
