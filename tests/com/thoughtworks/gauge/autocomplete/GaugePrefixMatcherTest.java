package com.thoughtworks.gauge.autocomplete;

import org.junit.Test;

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
    public void shouldMatchSimpleIncompleteStep() {
        String prefix = "This is";
        GaugePrefixMatcher gaugePrefixMatcher = new GaugePrefixMatcher(prefix);

        String name = "This is a step";
        assertTrue(gaugePrefixMatcher.prefixMatches(name));
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
}