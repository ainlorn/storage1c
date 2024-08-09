package com.msp31.storage1c.module.git;

import com.msp31.storage1c.utils.PathNormalizer;

class GitUtils {
    static String normalizeRelPath(String path) {
        return PathNormalizer.normalize(path);
    }
}
