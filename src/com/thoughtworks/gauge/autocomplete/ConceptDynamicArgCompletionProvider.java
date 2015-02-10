package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.language.psi.ConceptDynamicArg;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;

public class ConceptDynamicArgCompletionProvider extends CompletionProvider<CompletionParameters> {
    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        String prefix = getPrefix(parameters);
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(prefix));
        Collection<ConceptDynamicArg> args = PsiTreeUtil.collectElementsOfType(parameters.getOriginalFile(), ConceptDynamicArg.class);
        for (ConceptDynamicArg arg : args) {
            LookupElementBuilder item = LookupElementBuilder.create(arg.getText().replaceAll("<|>",""));
            resultSet.addElement(item);
        }
    }

}
