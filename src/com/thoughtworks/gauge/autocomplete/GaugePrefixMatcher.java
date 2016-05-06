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
        Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        String substring = "";
        if (!prefix.isEmpty()) substring = String.valueOf(prefix.charAt(0));
        Matcher matcher = pattern.matcher(substring);
        String escape = "";
        if (matcher.matches()) escape = "\\";
        return escape + prefix.replaceAll("\"[\\w ]+\"", "<[\\\\w ]*>");
    }
}
