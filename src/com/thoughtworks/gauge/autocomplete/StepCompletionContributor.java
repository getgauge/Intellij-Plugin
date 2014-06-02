package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.thoughtworks.gauge.core.GaugeConnection;
import com.thoughtworks.gauge.language.Specification;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import com.thoughtworks.gauge.util.SocketUtils;

import java.io.IOException;

public class StepCompletionContributor extends CompletionContributor {


    public StepCompletionContributor() throws IOException {

        GaugeConnection gaugeConnection = new GaugeConnection(SocketUtils.getApiPort());

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.STEP).withLanguage(Specification.INSTANCE), new StepCompletionProvider(gaugeConnection));
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.DYNAMIC_ARG).withLanguage(Specification.INSTANCE), new DynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.ARG).withLanguage(Specification.INSTANCE), new StaticArgCompletionProvider());
    }

}