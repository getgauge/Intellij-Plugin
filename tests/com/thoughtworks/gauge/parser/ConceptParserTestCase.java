package com.thoughtworks.gauge.parser;

import com.intellij.testFramework.ParsingTestCase;

import java.io.File;

public class ConceptParserTestCase extends ParsingTestCase {
    public ConceptParserTestCase() {
        super("", "cpt", new ConceptParserDefinition());
    }

    public void testSimpleConcept() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return new File("testdata", "conceptParser").getAbsolutePath();
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}