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

public class SpecColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Specification Heading", SpecSyntaxHighlighter.SPEC_HEADING),
            new AttributesDescriptor("Scenario Heading", SpecSyntaxHighlighter.SCENARIO_HEADING),
            new AttributesDescriptor("Step", SpecSyntaxHighlighter.STEP),
            new AttributesDescriptor("Comment", SpecSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Arguments", SpecSyntaxHighlighter.ARG),
            new AttributesDescriptor("Dynamic Arguments", SpecSyntaxHighlighter.DYNAMIC_ARG),
            new AttributesDescriptor("Table Header", SpecSyntaxHighlighter.TABLE_HEADER),
            new AttributesDescriptor("Table Border", SpecSyntaxHighlighter.TABLE_BORDER),
            new AttributesDescriptor("Table Item", SpecSyntaxHighlighter.TABLE_ROW),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new SpecSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "# Specification Heading\n" +
                "This comment explains what the spec intends to test\n" +
                "in multi line\n" +
                "|name                                     |type |\n" +
                "|-----------------------------------------|-----|\n" +
                "|manifest.json                            |file |\n" +
                "|specs                                    |dir  |\n" +
                "* This is a context\n" +
                "## Scenario Heading\n" +
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
        return "Specification";
    }
}