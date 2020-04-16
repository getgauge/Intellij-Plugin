/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

public class ArgQuoteHandler extends SimpleTokenSetQuoteHandler {
    public ArgQuoteHandler() {
        super(SpecTokenTypes.ARG_START);
    }
}
