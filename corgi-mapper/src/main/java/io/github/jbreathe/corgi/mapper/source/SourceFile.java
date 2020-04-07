package io.github.jbreathe.corgi.mapper.source;

import java.util.List;

/**
 * Wrapper for imports and private methods.
 */
public final class SourceFile {
    private final List<String> imports;
    private final List<SourceMethod> privateMethods;

    SourceFile(List<String> imports, List<SourceMethod> privateMethods) {
        this.imports = imports;
        this.privateMethods = privateMethods;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<SourceMethod> getPrivateMethods() {
        return privateMethods;
    }
}
