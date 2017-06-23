package com.thoughtworks.gauge.markdownPreview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownProcessorTest {
    @Test
    public void shouldAddSpanForTags() throws Exception {
        String html = new MarkdownProcessor().getHtml("This is a comment line.\ntags:abc");
        assertEquals("<p>This is a comment line.<br />\n<span class=\"tag\">tags:abc</span></p>\n", html);

        html = new MarkdownProcessor().getHtml("This is another comment line.\nTagS  :  abc,def");
        assertEquals("<p>This is another comment line.<br />\n<span class=\"tag\">TagS  :  abc,def</span></p>\n", html);
    }

    @Test
    public void shouldNotProcessTagsIfNotInComment() throws Exception {
        String html = new MarkdownProcessor().getHtml("* This is a comment line.\ntags:abc");
        assertEquals("<ul>\n<li>This is a comment line.<br />\ntags:abc</li>\n</ul>\n", html);

        html = new MarkdownProcessor().getHtml("`Tag : in a codeblock`");
        assertEquals("<p><code>Tag : in a codeblock</code></p>\n", html);

        html = new MarkdownProcessor().getHtml("[Tag : a](www.someurl.com)");
        assertEquals("<p><a href=\"www.someurl.com\">Tag : a</a></p>\n", html);
    }
}