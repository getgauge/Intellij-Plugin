/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import com.thoughtworks.gauge.lexer.SpecLexer;


public class SpecStepWordScanner extends DefaultWordsScanner {
    public SpecStepWordScanner() {
        super(new SpecLexer(), TokenSet.create(SpecTokenTypes.STEP), TokenSet.create(SpecTokenTypes.COMMENT), TokenSet.create(SpecTokenTypes.STEP));
    }
}
