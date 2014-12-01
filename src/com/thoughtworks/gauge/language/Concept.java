package com.thoughtworks.gauge.language;

import com.intellij.lang.Language;

public class Concept extends Language {
    public static final Concept INSTANCE = new Concept();

    private Concept() {
        super("Concept");
    }
}
