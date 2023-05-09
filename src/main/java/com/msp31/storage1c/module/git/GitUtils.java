package com.msp31.storage1c.module.git;

import java.io.File;
import java.nio.file.Path;

class GitUtils {
    static String normalizeRelPath(String path) {
        path = path
                .replace('\\', File.separatorChar)
                .replace('/', File.separatorChar);
        if (!path.startsWith(File.separator))
            path = File.separatorChar + path;
        return Path.of(path).normalize().toString().substring(1);
    }
}
