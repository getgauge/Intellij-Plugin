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

package com.thoughtworks.gauge.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import com.thoughtworks.gauge.lexer.SpecLexer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SpecSyntaxHighlighter extends SyntaxHighlighterBase {

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SpecLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(SpecTokenTypes.SPEC_HEADING)) {
            return HighlighterTokens.SPEC_HEADING_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.SCENARIO_HEADING)) {
            return HighlighterTokens.SCENARIO_HEADING_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.STEP)) {
            return HighlighterTokens.STEP_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.COMMENT)) {
            return HighlighterTokens.COMMENT_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TABLE_HEADER)) {
            return HighlighterTokens.TABLE_HEADER_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TABLE_ROW_VALUE)) {
            return HighlighterTokens.TABLE_ROW_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TAGS)) {
            return HighlighterTokens.TAGS_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TABLE_BORDER)) {
            return HighlighterTokens.TABLE_BORDER_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.ARG_START) || tokenType.equals(SpecTokenTypes.ARG) || tokenType.equals(SpecTokenTypes.ARG_END)) {
            return HighlighterTokens.ARG_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.DYNAMIC_ARG_START) || tokenType.equals(SpecTokenTypes.DYNAMIC_ARG) || tokenType.equals(SpecTokenTypes.DYNAMIC_ARG_END)) {
            return HighlighterTokens.DYNAMIC_ARG_ATTRIBUTE;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return HighlighterTokens.BAD_CHAR_KEYS;
        } else if (tokenType.equals((SpecTokenTypes.KEYWORD))) {
            return HighlighterTokens.KEYWORD_ATTRIBUTE;
        } else {
            return HighlighterTokens.EMPTY_KEYS;
        }
    }
}