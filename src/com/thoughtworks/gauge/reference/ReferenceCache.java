package com.thoughtworks.gauge.reference;


import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.SpecStep;

import java.util.Hashtable;

public class ReferenceCache {

    private final Hashtable<String, PsiStepReferenceCache> stepReferences;

    public ReferenceCache() {
        this.stepReferences = new Hashtable<>();
    }

    public PsiElement searchReferenceFor(SpecStep step) {
        try {
            String stepValueText = step.getStepValue().getStepText();
            PsiStepReferenceCache element = stepReferences.get(stepValueText);
            if (isValid(element)) {
                return element.getPsiElement();
            }
            stepReferences.remove(stepValueText);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void addStepReference(SpecStep step, PsiElement referenceElement) {
        if (isValidPsiElement(referenceElement)) {
            stepReferences.put(step.getStepValue().getStepText(), new PsiStepReferenceCache(referenceElement.getText(), referenceElement));
        }
    }

    private boolean isValidPsiElement(PsiElement psiElement) {
        return psiElement != null && psiElement.isValid();
    }

    private boolean isValid(PsiStepReferenceCache element) {
        if (element == null || element.getPsiElement() == null) {
            return false;
        }
        PsiElement psiElement = element.getPsiElement();
        return psiElement.isValid() && element.getText().equals(psiElement.getText());
    }

    private class PsiStepReferenceCache {
        private String text;
        private PsiElement psiElement;

        public PsiStepReferenceCache(String text, PsiElement psiElement) {
            this.text = text;
            this.psiElement = psiElement;
        }

        public String getText() {
            return text;
        }

        public PsiElement getPsiElement() {
            return psiElement;
        }
    }
}
