package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.language.Specification;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepCompletionContributor extends CompletionContributor {

    private static final List<String> ALL_STEPS = new ArrayList<String>() {{
        add("enter {} and {}");
        add("login");
        add("add detail for user {}");
        add("{} is expected");
    }};

    public StepCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(SpecTokenTypes.STEP).withLanguage(Specification.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        for (String step : getStepsInProject()) {
                            LookupElementBuilder element = LookupElementBuilder.create(step);
                            resultSet.addElement(element.withInsertHandler(new InsertHandler<LookupElement>() {
                                @Override
                                public void handleInsert(InsertionContext context, LookupElement item) {
                                    PsiElement stepElement = context.getFile().findElementAt(context.getStartOffset());
                                    final TemplateBuilder builder = TemplateBuilderFactory.getInstance().createTemplateBuilder(stepElement);
                                    Pattern pattern = Pattern.compile("\\{\\}");
                                    if (stepElement != null) {
                                        Matcher matcher = pattern.matcher(stepElement.getText());
                                        while (matcher.find()) {
                                            builder.replaceRange(new TextRange(matcher.start(), matcher.end()), "\"arg\"");
                                        }
                                        builder.run(context.getEditor(), false);
                                    }
                                }
                            }));
                        }
                    }
                }
        );


        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.DYNAMIC_ARG).withLanguage(Specification.INSTANCE), new DynamicArgCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SpecTokenTypes.ARG).withLanguage(Specification.INSTANCE), new StaticArgCompletionProvider());
    }

    private List<String> getStepsInProject() {
        return ALL_STEPS;
    }


}