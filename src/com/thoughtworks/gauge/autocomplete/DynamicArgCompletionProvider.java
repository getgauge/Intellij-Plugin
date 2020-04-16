/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.language.psi.SpecDetail;
import com.thoughtworks.gauge.language.psi.SpecTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;

public class DynamicArgCompletionProvider extends CompletionProvider<CompletionParameters> {
    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        String prefix = getPrefix(parameters);
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(prefix));
        SpecDetail specDetail = PsiTreeUtil.getChildOfType(parameters.getOriginalFile(), SpecDetail.class);
        if (specDetail == null)
            return;
        SpecTable table = specDetail.getDataTable();
        if (table != null) {
            List<String> headers = table.getTableHeader().getHeaders();
            for (String header : headers) {
                LookupElementBuilder item = LookupElementBuilder.create(header);
                resultSet.addElement(item);
            }
        }
    }

}
