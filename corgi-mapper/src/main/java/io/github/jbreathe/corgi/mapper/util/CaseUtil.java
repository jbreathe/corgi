package io.github.jbreathe.corgi.mapper.util;

import org.jetbrains.annotations.NotNull;

public final class CaseUtil {
    private CaseUtil() {
    }

    public static String toUpperCamel(@NotNull String in) {
        if (in.isEmpty()) {
            return in;
        }
        return Character.toUpperCase(in.charAt(0)) + in.substring(1);
    }
}
