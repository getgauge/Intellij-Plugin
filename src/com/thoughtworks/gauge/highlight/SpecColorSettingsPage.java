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
                "in multi line" +
                "* This is a context\n" +
                "## Scenario Heading\n" +
                "* Step 1\n" +
                "comments between steps" +
                "* Step 2\n";
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