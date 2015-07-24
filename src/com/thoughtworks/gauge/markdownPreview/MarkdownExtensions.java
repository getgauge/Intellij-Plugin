package com.thoughtworks.gauge.markdownPreview;

import org.pegdown.Extensions;

public class MarkdownExtensions {
    private final int parsingTimeout = 10000;

    public int getParsingTimeout() {
        return parsingTimeout;
    }

    public int getExtensionsValue() {
        return (Extensions.SMARTS) +
                (Extensions.QUOTES) +
                (Extensions.ABBREVIATIONS) +
                (Extensions.HARDWRAPS) +
                (Extensions.AUTOLINKS) +
                (Extensions.WIKILINKS) +
                (Extensions.TABLES) +
                (Extensions.DEFINITIONS) +
                (Extensions.STRIKETHROUGH);
    }
}
