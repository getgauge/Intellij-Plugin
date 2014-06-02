package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

public class ArgQuoteHandler extends SimpleTokenSetQuoteHandler {
    public ArgQuoteHandler() {
        super(SpecTokenTypes.ARG_START);
    }
}
