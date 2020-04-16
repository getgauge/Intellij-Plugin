/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.thoughtworks.gauge.language.token.ConceptTokenTypes;
import com.thoughtworks.gauge.lexer.ConceptLexer;


public class ConceptStepWordScanner extends DefaultWordsScanner {
    public ConceptStepWordScanner() {
        super(new ConceptLexer(), TokenSet.create(ConceptTokenTypes.STEP), TokenSet.create(ConceptTokenTypes.COMMENT), TokenSet.create(ConceptTokenTypes.STEP));
    }
}
