package com.thoughtworks.gauge.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import com.thoughtworks.gauge.language.token.SpecTokenType;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import com.thoughtworks.gauge.lexer.SpecLexer;


public class SpecStepWordScanner extends DefaultWordsScanner {

    public SpecStepWordScanner() {
        super(new SpecLexer(), TokenSet.create(SpecTokenTypes.STEP), TokenSet.create(SpecTokenTypes.COMMENT), TokenSet.create(SpecTokenTypes.STEP, SpecTokenTypes.SPEC_HEADING, SpecTokenTypes.TABLE, SpecTokenTypes.TAGS, SpecTokenTypes.SCENARIO));
    }
}

