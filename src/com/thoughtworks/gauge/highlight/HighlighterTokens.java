/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Tokens indicating the text attributes that will be applied for syntax highlighting. It is tied to other Intellij
 * syntax highlighting concepts (e.g. static variables, constants, line comments). The exact colors and such are
 * determined by the user's IDE theme and preferences in the IDE Settings.
 */
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
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER");
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
