package com.thoughtworks.gauge.markdownPreview;


import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

class MarkdownProcessor {
    String getHtml(String markdownText) {
        MutableDataSet options = getOptions();

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdownText);
        return renderer.render(document);
    }

    @NotNull
    private MutableDataSet getOptions() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                TypographicExtension.create(),
                AutolinkExtension.create(),
                AbbreviationExtension.create(),
                WikiLinkExtension.create(),
                DefinitionExtension.create(),
                StrikethroughExtension.create()
        ));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        return options;
    }
}
