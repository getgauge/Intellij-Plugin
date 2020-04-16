/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ReferenceRange;
import com.thoughtworks.gauge.language.Concept;
import com.thoughtworks.gauge.language.Specification;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.token.ConceptTokenTypes;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

import java.io.IOException;
import java.util.List;

public class StepCompletionContributor extends CompletionContributor {

    public StepCompletionContributor() throws IOException {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.STEP).withLanguage(Specification.INSTANCE), new StepCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.DYNAMIC_ARG).withLanguage(Specification.INSTANCE), new DynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.ARG).withLanguage(Specification.INSTANCE), new StaticArgCompletionProvider());
        StepCompletionProvider provider = new StepCompletionProvider();
        provider.setConcept(true);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ConceptTokenTypes.STEP).withLanguage(Concept.INSTANCE), provider);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ConceptTokenTypes.DYNAMIC_ARG).withLanguage(Concept.INSTANCE), new ConceptDynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ConceptTokenTypes.ARG).withLanguage(Concept.INSTANCE), new ConceptStaticArgCompletionProvider());
    }

    public static String getPrefix(CompletionParameters parameters) {
        PsiElement insertedElement = parameters.getPosition();
        int offsetInFile = parameters.getOffset();

        PsiReference ref = insertedElement.getContainingFile().findReferenceAt(offsetInFile);
        if (isStep(insertedElement) && ref != null) {
            List<TextRange> ranges = ReferenceRange.getRanges(ref);
            PsiElement element = ref.getElement();
            int startOffset = element.getTextRange().getStartOffset();
            for (TextRange range : ranges) {
                if (range.contains(offsetInFile - startOffset)) {
                    int endIndex = offsetInFile - startOffset;
                    int startIndex = range.getStartOffset();
                    return StringUtil.trimStart(element.getText().substring(startIndex + 1, endIndex), " ");
                }
            }

        }
        return parameters.getPosition().getText().replace("IntellijIdeaRulezzz ", "").trim();
    }

    private static boolean isStep(PsiElement insertedElement) {
        return insertedElement.getContext() instanceof SpecStep;
    }
}
