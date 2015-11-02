package com.thoughtworks.gauge.markdownPreview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormatterTest {

    @Test
    public void testFormat() throws Exception {
        String text = "Steps Collection\n" +
                "================\n" +
                "\n" +
                "tags: api\n" +
                "\n" +
                "* In an empty directory initialize a project with the <current> language\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n" +
                "    |step text|implementation         |\n" +
                "    |---------|-----------------------|\n" +
                "    |context 1|\"inside first context\" |\n" +
                "    |context 2|\"inside second context\"|\n";
        String actual = Formatter.format(text);

        String expected = "Steps Collection\n" +
                "================\n" +
                "\n" +
                "tags: api\n" +
                "\n" +
                "* In an empty directory initialize a project with the &lt;current&gt; language\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n\n" +
                "\t|step text|implementation         |\n" +
                "\t|---------|-----------------------|\n" +
                "\t|context 1|\"inside first context\" |\n" +
                "\t|context 2|\"inside second context\"|\n";

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatWithMultipleTables() throws Exception {
        String text = "Steps Collection\n" +
                "================\n" +
                "\n" +
                "tags: api\n" +
                "\n" +
                "* In an empty directory initialize a project with the <current> language\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n" +
                "    |step text|implementation         |\n" +
                "    |---------|-----------------------|\n" +
                "    |context 1|\"inside first context\" |\n" +
                "    |context 2|\"inside second context\"|\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n" +
                "        |step text|implementation         |\n" +
                "    |---------|-----------------------|\n" +
                "       |context 1|\"inside first context\" |\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n\n\n" +
                "    |step text|implementation         |\n" +
                "    |---------|-----------------------|\n" +
                "    |context 1|\"inside first context\" |\n";
        String actual = Formatter.format(text);

        String expected = "Steps Collection\n" +
                "================\n" +
                "\n" +
                "tags: api\n" +
                "\n" +
                "* In an empty directory initialize a project with the &lt;current&gt; language\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n\n" +
                "\t|step text|implementation         |\n" +
                "\t|---------|-----------------------|\n" +
                "\t|context 1|\"inside first context\" |\n" +
                "\t|context 2|\"inside second context\"|\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n\n" +
                "\t|step text|implementation         |\n" +
                "\t|---------|-----------------------|\n" +
                "\t|context 1|\"inside first context\" |\n" +
                "* Create a specification \"Specification 1\" with the following contexts\n\n" +
                "\t|step text|implementation         |\n" +
                "\t|---------|-----------------------|\n" +
                "\t|context 1|\"inside first context\" |\n";

        assertEquals(expected, actual);
    }
}