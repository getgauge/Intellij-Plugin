package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.thoughtworks.gauge.language.Specification;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

import java.io.IOException;

public class StepCompletionContributor extends CompletionContributor {

    public StepCompletionContributor() throws IOException {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.STEP).withLanguage(Specification.INSTANCE), new StepCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.DYNAMIC_ARG).withLanguage(Specification.INSTANCE), new DynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.ARG).withLanguage(Specification.INSTANCE), new StaticArgCompletionProvider());
    }

}