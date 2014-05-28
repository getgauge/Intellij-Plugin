package com.thoughtworks.gauge.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
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
    public static final TextAttributesKey SPEC_HEADING = createTextAttributesKey("SPEC_HEADING", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SCENARIO_HEADING = createTextAttributesKey("SCENARIO_HEADING", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey STEP = createTextAttributesKey("STEP", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey TABLE_HEADER = createTextAttributesKey("TABLE_HEADER", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey TABLE_ROW = createTextAttributesKey("TABLE_ROW", DefaultLanguageHighlighterColors.DOC_COMMENT);
    public static final TextAttributesKey ARG = createTextAttributesKey("ARG", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey DYNAMIC_ARG = createTextAttributesKey("DYNAMIC ARG", DefaultLanguageHighlighterColors.CONSTANT);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
            new TextAttributes(Color.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] SPEC_HEADING_ATTRIBUTE = new TextAttributesKey[]{SPEC_HEADING};
    private static final TextAttributesKey[] SCENARIO_HEADING_ATTRIBUTE = new TextAttributesKey[]{SCENARIO_HEADING};
    private static final TextAttributesKey[] STEP_ATTRIBUTE = new TextAttributesKey[]{STEP};
    private static final TextAttributesKey[] COMMENT_ATTRIBUTE = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] TABLE_HEADER_ATTRIBUTE = new TextAttributesKey[]{TABLE_HEADER};
    private static final TextAttributesKey[] TABLE_ROW_ATTRIBUTE = new TextAttributesKey[]{TABLE_ROW};
    private static final TextAttributesKey[] ARG_ATTRIBUTE = new TextAttributesKey[]{ARG};
    private static final TextAttributesKey[] DYNAMIC_ARG_ATTRIBUTE = new TextAttributesKey[]{DYNAMIC_ARG};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SpecLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {

        if (tokenType.equals(SpecTokenTypes.SPEC_HEADING)) {
            return SPEC_HEADING_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.SCENARIO_HEADING)) {
            return SCENARIO_HEADING_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.STEP)) {
            return STEP_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.COMMENT)) {
            return COMMENT_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TABLE_HEADER)) {
            return TABLE_HEADER_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.TABLE_ROW)) {
            return TABLE_ROW_ATTRIBUTE;
        } else if (tokenType.equals(SpecTokenTypes.ARG_START) || tokenType.equals(SpecTokenTypes.ARG) || tokenType.equals(SpecTokenTypes.ARG_END)) {
            return ARG_ATTRIBUTE;
        }else if (tokenType.equals(SpecTokenTypes.DYNAMIC_ARG_START) || tokenType.equals(SpecTokenTypes.DYNAMIC_ARG) || tokenType.equals(SpecTokenTypes.DYNAMIC_ARG_END)) {
            return DYNAMIC_ARG_ATTRIBUTE;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}