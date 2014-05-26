package com.thoughtworks.gauge.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;

public interface SpecStep extends SpecNamedElement {

    String getStepName();

    String getName();

    PsiElement setName(String newName);

    PsiElement getNameIdentifier();

    ItemPresentation getPresentation();

    SpecTable getInlineTable();
}
