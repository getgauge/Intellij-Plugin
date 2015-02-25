// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

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