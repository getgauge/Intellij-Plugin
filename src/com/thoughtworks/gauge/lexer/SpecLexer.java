package com.thoughtworks.gauge.lexer;

import com.intellij.lexer.FlexAdapter;

public class SpecLexer extends FlexAdapter {
    public SpecLexer() {
        super(new _SpecLexer());
    }
}
