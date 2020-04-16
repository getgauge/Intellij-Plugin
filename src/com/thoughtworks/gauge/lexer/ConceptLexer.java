/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * An adapter for integrating with the auto-generated _ConceptLexer created by _ConceptLexer.flex
 * It is used to break .cpt file text into semantic tokens.
 *
 * Use of this code, and how to generate _ConceptLexer from the .flex file, can be found at
 * http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/lexer_and_parser_definition.html
 */
public class ConceptLexer extends FlexAdapter {
    public ConceptLexer() {
        super(new _ConceptLexer());
    }
}
