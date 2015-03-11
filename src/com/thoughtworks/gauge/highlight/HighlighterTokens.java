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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class HighlighterTokens {
    public static final TextAttributesKey SPEC_HEADING = createTextAttributesKey("SPEC_HEADING", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SCENARIO_HEADING = createTextAttributesKey("SCENARIO_HEADING", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey STEP = createTextAttributesKey("STEP", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey TABLE_HEADER = createTextAttributesKey("TABLE_HEADER", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey TABLE_ROW = createTextAttributesKey("TABLE_ROW", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey ARG = createTextAttributesKey("ARG", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey DYNAMIC_ARG = createTextAttributesKey("DYNAMIC ARG", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey TABLE_BORDER = createTextAttributesKey("TABLE BORDER", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey TAGS = createTextAttributesKey("TAGS", DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
            new TextAttributes(Color.RED, null, null, null, Font.BOLD));
    public static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    public static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    public static final TextAttributesKey[] SPEC_HEADING_ATTRIBUTE = new TextAttributesKey[]{SPEC_HEADING};
    public static final TextAttributesKey[] SCENARIO_HEADING_ATTRIBUTE = new TextAttributesKey[]{SCENARIO_HEADING};
    public static final TextAttributesKey[] STEP_ATTRIBUTE = new TextAttributesKey[]{STEP};
    public static final TextAttributesKey[] COMMENT_ATTRIBUTE = new TextAttributesKey[]{COMMENT};
    public static final TextAttributesKey[] TAGS_ATTRIBUTE = new TextAttributesKey[]{TAGS};
    public static final TextAttributesKey[] KEYWORD_ATTRIBUTE = new TextAttributesKey[]{KEYWORD};
    public static final TextAttributesKey[] TABLE_HEADER_ATTRIBUTE = new TextAttributesKey[]{TABLE_HEADER};
    public static final TextAttributesKey[] TABLE_ROW_ATTRIBUTE = new TextAttributesKey[]{TABLE_ROW};
    public static final TextAttributesKey[] ARG_ATTRIBUTE = new TextAttributesKey[]{ARG};
    public static final TextAttributesKey[] DYNAMIC_ARG_ATTRIBUTE = new TextAttributesKey[]{DYNAMIC_ARG};
    public static final TextAttributesKey[] TABLE_BORDER_ATTRIBUTE = new TextAttributesKey[]{TABLE_BORDER};
}
