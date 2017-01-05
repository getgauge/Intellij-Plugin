package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import com.thoughtworks.gauge.language.token.ConceptTokenTypes;

public class ConceptInspectionProvider extends GaugeInspectionProvider {
    PsiElement getElement(PsiElement element) {
        if (element == null) return null;
        if (element instanceof ConceptStep) return element;
        if (element instanceof LeafPsiElement && ((LeafPsiElement) element).getElementType().equals(ConceptTokenTypes.CONCEPT_HEADING))
            return element;
        return getElement(element.getParent());
    }
}
