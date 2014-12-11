package com.thoughtworks.gauge.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class ConceptColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Concept Heading", HighlighterTokens.SPEC_HEADING),
            new AttributesDescriptor("Step", HighlighterTokens.STEP),
            new AttributesDescriptor("Comment", HighlighterTokens.COMMENT),
            new AttributesDescriptor("Arguments", HighlighterTokens.ARG),
            new AttributesDescriptor("Dynamic Arguments", HighlighterTokens.DYNAMIC_ARG),
            new AttributesDescriptor("Table Header", HighlighterTokens.TABLE_HEADER),
            new AttributesDescriptor("Table Border", HighlighterTokens.TABLE_BORDER),
            new AttributesDescriptor("Table Item", HighlighterTokens.TABLE_ROW),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ConceptSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "# Concept Heading\n" +
                "This comment explains what the spec intends to test\n" +
                "* Step 1 with \"arg\"\n" +
                "* Step 2 with <dynamic arg>\n" +
                "comments between steps\n" +
                "* Step 2\n" +
                "|id|filename|\n" +
                "|1 |foo     |\n" +
                "|2 |bar     |\n" +
                "|3 |<name>  |\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Concept";
    }
}
