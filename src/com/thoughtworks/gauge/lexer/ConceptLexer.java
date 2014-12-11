package com.thoughtworks.gauge.lexer;

import com.intellij.lexer.FlexAdapter;

public class ConceptLexer extends FlexAdapter {
    public ConceptLexer() {
        super(new _ConceptLexer());
    }
}
