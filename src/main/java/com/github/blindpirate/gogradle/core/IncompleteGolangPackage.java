package com.github.blindpirate.gogradle.core;

import com.github.blindpirate.gogradle.vcs.VcsType;

import java.util.Optional;

public class IncompleteGolangPackage extends GolangPackage {
    private IncompleteGolangPackage(String path) {
        super(path);
    }

    @Override
    public String getRootPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VcsType getVcsType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Optional<GolangPackage> longerPath(String packagePath) {
        return Optional.empty();
    }

    @Override
    protected Optional<GolangPackage> shorterPath(String packagePath) {
        return Optional.of(of(packagePath));
    }

    public static IncompleteGolangPackage of(String path) {
        return new IncompleteGolangPackage(path);
    }
}
