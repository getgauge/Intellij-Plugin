// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

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
