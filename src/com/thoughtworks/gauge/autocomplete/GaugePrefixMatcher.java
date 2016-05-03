package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.PrefixMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GaugePrefixMatcher extends PrefixMatcher {

    private Pattern regexPattern;

    protected GaugePrefixMatcher(String prefix) {
        super(prefix);
        regexPattern = Pattern.compile(toRegex(this.getPrefix()), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean prefixMatches(@NotNull String name) {
        Matcher matcher = regexPattern.matcher(name);
        return matcher.find();
    }

    @NotNull
    @Override
    public PrefixMatcher cloneWithPrefix(@NotNull String prefix) {
        return new GaugePrefixMatcher(prefix);
    }

    private static String toRegex(String prefix) {
        String result = prefix.replaceAll("([^a-zA-Z0-9 ])", "\\\\$1");
        result = result.replaceAll("(\\\\[\"'])(?:(?=(\\\\{0,2}))\\2.)*?\\1", "<[\\\\w\\\\s]+>");
        return result;
    }
}
