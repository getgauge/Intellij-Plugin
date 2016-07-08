package com.thoughtworks.gauge.findUsages.helper;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.thoughtworks.gauge.findUsages.StepCollector;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReferenceSearchHelper {

    public static final String UNKNOWN_SCOPE = "<unknown scope>";
    private final ModuleHelper helper;

    public ReferenceSearchHelper() {
        helper = new ModuleHelper();
    }

    public ReferenceSearchHelper(ModuleHelper helper) {
        this.helper = helper;
    }

    public boolean shouldFindReferences(@NotNull ReferencesSearch.SearchParameters searchParameters, PsiElement element) {
        return helper.isGaugeModule(element) &&
                !searchParameters.getScope().getDisplayName().equals(UNKNOWN_SCOPE) &&
                GaugeUtil.isGaugeElement(element);
    }

    @NotNull
    public StepCollector getStepCollector(PsiElement element) {
        return new StepCollector(element.getProject());
    }

    @NotNull
    public List<PsiElement> getPsiElements(StepCollector collector, PsiElement element) {
        List<PsiElement> elements = new ArrayList<>();
        if (element.getClass().equals(ConceptStepImpl.class))
            elements = collector.get(getConceptStepText((ConceptStepImpl) element));
        else if (element.getClass().equals(PsiMethodImpl.class))
            for (String alias : StepUtil.getGaugeStepAnnotationValues((PsiMethod) element))
                elements.addAll(collector.get(getStepText(alias, element)));
        else if (element.getClass().equals(SpecStepImpl.class)) {
            elements = collector.get(getStepText((SpecStepImpl) element));
            elements.addAll(collector.get(((SpecStepImpl) element).getName()));
        }
        return elements;
    }

    private String getConceptStepText(ConceptStepImpl element) {
        String text = element.getStepValue().getStepText().trim();
        return !text.equals("") && text.charAt(0) == '*' || text.charAt(0) == '#' ? text.substring(1).trim() : text;
    }

    private String getStepText(SpecStep elementToSearch) {
        return elementToSearch.getStepValue().getStepText().trim();
    }

    private String getStepText(String text, PsiElement element) {
        return SpecPsiImplUtil.getStepValueFor(element, text.charAt(0) == '"' ? text.substring(1, text.length() - 1) : text, false).getStepText();
    }
}