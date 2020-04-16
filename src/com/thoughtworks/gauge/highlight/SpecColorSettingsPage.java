/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

/**
 * The page that appears in the Intellij IDEA Settings page, allowing the user to override the default appearances of
 * syntax elements (headers, comments, etc) within Gauge specification (.spec) files.
 */
public class SpecColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Specification Heading", HighlighterTokens.SPEC_HEADING),
            new AttributesDescriptor("Scenario Heading", HighlighterTokens.SCENARIO_HEADING),
            new AttributesDescriptor("Step", HighlighterTokens.STEP),
            new AttributesDescriptor("Comment", HighlighterTokens.COMMENT),
            new AttributesDescriptor("Arguments", HighlighterTokens.ARG),
            new AttributesDescriptor("Dynamic Arguments", HighlighterTokens.DYNAMIC_ARG),
            new AttributesDescriptor("Table Header", HighlighterTokens.TABLE_HEADER),
            new AttributesDescriptor("Table Border", HighlighterTokens.TABLE_BORDER),
            new AttributesDescriptor("Table Item", HighlighterTokens.TABLE_ROW),
            new AttributesDescriptor("Tags", HighlighterTokens.TAGS),

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
                "tags: first, second" +
                "This comment explains what the spec intends to test\n" +
                "in multi line\n" +
                "|name                                     |type |\n" +
                "|-----------------------------------------|-----|\n" +
                "|manifest.json                            |file |\n" +
                "|specs                                    |dir  |\n" +
                "* This is a context\n" +
                "## Scenario Heading\n" +
                "Tags: tag3, tag4" +
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
