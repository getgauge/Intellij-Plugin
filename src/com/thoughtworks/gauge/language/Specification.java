package com.thoughtworks.gauge.language;

import com.intellij.lang.Language;

public class Specification extends Language {
    public static final Specification INSTANCE = new Specification();
 
    private Specification() {
        super("Specification");
    }
}