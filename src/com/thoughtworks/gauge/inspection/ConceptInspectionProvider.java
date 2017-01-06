package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.ConceptConceptHeading;
import com.thoughtworks.gauge.language.psi.ConceptStep;

public class ConceptInspectionProvider extends GaugeInspectionProvider {
    PsiElement getElement(PsiElement element) {
        if (element == null) return null;
        if (element instanceof ConceptStep || element instanceof ConceptConceptHeading) return element;
        return getElement(element.getParent());
    }
}
