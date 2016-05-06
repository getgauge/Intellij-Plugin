package com.thoughtworks.gauge.autocomplete;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GaugePrefixMatcherTest {

    @Test
    public void shouldMatchSimpleCompleteStep() {
        String prefix = "This is a step";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }

    @Test
    public void shouldMatchCaseInsensitiveSimpleCompleteStep() {
        String prefix = "this is a Step";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }

    @Test
    public void shouldMatchSimpleIncompleteStep() {
        String prefix = "This is";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }

    @Test
    public void shouldMatchStepWithSpecialChars() {
        GaugePrefixMatcher gaugePrefixMatcher;

        gaugePrefixMatcher = new GaugePrefixMatcher("+This is");
        String name = "+This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("*This is");
        name = "*This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("?This is");
        name = "?This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("^This is");
        name = "^This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("(This is");
        name = "(This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("{This is");
        name = "{This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("$This is");
        name = "$This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("[This is");
        name = "[This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));

    }

    @Test
    public void shouldNotMatchStepWithDifferentStepWithSpecialChars() {
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher("+The fd");
        String name = "+This is a step";
        assertFalse(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("*The fd");
        name = "*This is a step";
        assertFalse(gaugePrefixMatcher.prefixMatches(name));

        gaugePrefixMatcher = new GaugePrefixMatcher("?The fd");
        name = "?This is a step";
        assertFalse(gaugePrefixMatcher.prefixMatches(name));
    }

   @Test
    public void shouldMatchParameterizedCompleteStep() {
        String prefix = "Say \"hello\" to \"world\"";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "Say <hello again> to <someone>";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }

    @Test
    public void shouldMatchParameterizedIncompleteStep() {
        String prefix = "Say \"hello\" to ";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "Say <greetings> to <someone>";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }

    @Test
    public void shouldMatchEmptyString() {
        String prefix = "";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "Say <greetings> to <someone>";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
    }
}