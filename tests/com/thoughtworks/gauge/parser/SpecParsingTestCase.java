package com.thoughtworks.gauge.parser;

import com.intellij.testFramework.ParsingTestCase;

import java.io.File;

public class SpecParsingTestCase extends ParsingTestCase {
    public SpecParsingTestCase() {
        super("", "spec", new SpecParserDefinition());
    }
 
    public void testSimpleSpec() {
        doTest(true);
    }

    public void testSpecWithDataTable() {
        doTest(true);
    }
 
    @Override
    protected String getTestDataPath() {
        return new File("testdata", "specParser").getAbsolutePath();
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