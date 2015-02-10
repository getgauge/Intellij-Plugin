package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.language.psi.ConceptStaticArg;

import java.util.Collection;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;


public class ConceptStaticArgCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(CompletionParameters parameters, ProcessingContext processingContext, CompletionResultSet resultSet) {
        String prefix = getPrefix(parameters);
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(prefix));
        Collection<ConceptStaticArg> staticArgs = PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), ConceptStaticArg.class);
        for (ConceptStaticArg arg : staticArgs) {
            if (arg != null) {
                String text = arg.getText().replaceFirst("\"", "");
                String textWithoutQuotes = text.substring(0, text.length() - 1);
                if (!textWithoutQuotes.equals(""))
                    resultSet.addElement(LookupElementBuilder.create(textWithoutQuotes));
            }
        }
    }
}
