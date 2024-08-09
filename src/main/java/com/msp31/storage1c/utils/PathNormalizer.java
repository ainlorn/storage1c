package com.msp31.storage1c.utils;

import java.io.File;
import java.nio.file.Path;

public class PathNormalizer {
    public static String normalize(String path) {
        path = path
                .replace('\\', File.separatorChar)
                .replace('/', File.separatorChar);
        if (!path.startsWith(File.separator))
            path = File.separatorChar + path;
        return Path.of(path).normalize().toString().substring(1);
    }
}
