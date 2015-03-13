package com.thoughtworks.gauge.extract;

import com.intellij.openapi.ui.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractConceptInputValidator implements InputValidator {
    private final List<String> tagValues = new ArrayList<String>();

    public ExtractConceptInputValidator(String originalText) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        final Matcher matcher = pattern.matcher(originalText);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
    }

    @Override
    public boolean checkInput(String s) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        final Matcher matcher = pattern.matcher(s);
        int count = 0;
        while (matcher.find()) {
            count++;
            if (!tagValues.contains(matcher.group(1))) {
                return false;
            }
        }
        return count == tagValues.size();
    }

    @Override
    public boolean canClose(String s) {
        return true;
    }
}
