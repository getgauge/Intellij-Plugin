/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * An adapter for integrating with the auto-generated _SpecLexer created by _SpecLexer.flex.
 * It is used to break .spec file text into semantic tokens.
 *
 * Use of this code, and how to generate _SpecLexer from the .flex file, can be found at
 * http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/lexer_and_parser_definition.html
 */
public class SpecLexer extends FlexAdapter {
    public SpecLexer() {
        super(new _SpecLexer());
    }
}
